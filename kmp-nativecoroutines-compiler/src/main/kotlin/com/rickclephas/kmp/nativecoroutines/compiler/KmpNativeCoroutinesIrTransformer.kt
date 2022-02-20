package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.utils.*
import com.rickclephas.kmp.nativecoroutines.compiler.utils.isNativeCoroutinesFunction
import com.rickclephas.kmp.nativecoroutines.compiler.utils.referenceNativeSuspendFunction
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.passTypeArgumentsFrom
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

internal class KmpNativeCoroutinesIrTransformer(
    private val context: IrPluginContext,
    private val nameGenerator: NameGenerator,
    propagatedExceptions: List<FqName>,
    private val useThrowsAnnotation: Boolean
): IrElementTransformerVoidWithContext() {

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
        if (declaration.isFakeOverride || declaration.getter?.body != null || declaration.setter != null)
            return super.visitPropertyNew(declaration)

        val isNativeName = nameGenerator.isNativeName(declaration.name)
        val isNativeValueName = nameGenerator.isNativeValueName(declaration.name)
        val isNativeReplayCacheName = nameGenerator.isNativeReplayCacheName(declaration.name)
        if (!isNativeName && !isNativeValueName && !isNativeReplayCacheName)
            return super.visitPropertyNew(declaration)

        val getter = declaration.getter ?: return super.visitPropertyNew(declaration)
        val originalName = nameGenerator.createOriginalName(declaration.name)
        val originalProperty = declaration.parentAsClass.properties.single {
            it.name == originalName && it.needsNativeProperty
        }
        val originalGetter = originalProperty.getter
            ?: throw IllegalStateException("Original property doesn't have a getter")

        getter.body = when {
            isNativeName -> createNativeBody(getter, originalGetter)
            isNativeValueName -> createNativeValueBody(getter, originalGetter)
            isNativeReplayCacheName -> createNativeReplayCacheBody(getter, originalGetter)
            else -> throw IllegalStateException("Unsupported property type")
        }
        return super.visitPropertyNew(declaration)
    }

    private val stateFlowValueProperty = context.referenceStateFlowValueProperty()

    private fun createNativeValueBody(getter: IrFunction, originalGetter: IrSimpleFunction): IrBlockBody {
        val originalReturnType = originalGetter.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalGetter.returnType}")
        return DeclarationIrBuilder(context, getter.symbol,
            originalGetter.startOffset, originalGetter.endOffset).irBlockBody {
            val returnType = originalReturnType.getStateFlowValueTypeOrNull()?.typeOrNull
                ?: throw IllegalStateException("Unsupported StateFlow value type $originalReturnType")
            val valueGetter = stateFlowValueProperty.owner.getter?.symbol
                ?: throw IllegalStateException("Couldn't find StateFlow value getter")
            +irReturn(irCall(valueGetter, returnType).apply {
                dispatchReceiver = callOriginalFunction(getter, originalGetter)
            })
        }
    }

    private val sharedFlowReplayCacheProperty = context.referenceSharedFlowReplayCacheProperty()

    private fun createNativeReplayCacheBody(getter: IrFunction, originalGetter: IrSimpleFunction): IrBlockBody {
        val originalReturnType = originalGetter.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalGetter.returnType}")
        return DeclarationIrBuilder(context, getter.symbol,
            originalGetter.startOffset, originalGetter.endOffset).irBlockBody {
            val valueType = originalReturnType.getSharedFlowValueTypeOrNull()?.typeOrNull as? IrSimpleTypeImpl
                ?: throw IllegalStateException("Unsupported StateFlow value type $originalReturnType")
            val returnType = context.irBuiltIns.listClass.typeWith(listOf(valueType))
            val valueGetter = sharedFlowReplayCacheProperty.owner.getter?.symbol
                ?: throw IllegalStateException("Couldn't find StateFlow value getter")
            +irReturn(irCall(valueGetter, returnType).apply {
                dispatchReceiver = callOriginalFunction(getter, originalGetter)
            })
        }
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.isFakeOverride ||
            !nameGenerator.isNativeName(declaration.name) ||
            !declaration.isNativeCoroutinesFunction ||
            declaration.body != null
        ) return super.visitFunctionNew(declaration)
        val originalName = nameGenerator.createOriginalName(declaration.name)
        val originalFunction = declaration.parentAsClass.functions.single {
            it.name == originalName && it.needsNativeFunction && it.valueParameters.areSameAs(declaration.valueParameters)
        }
        declaration.body = createNativeBody(declaration, originalFunction)
        return super.visitFunctionNew(declaration)
    }

    private val nativeSuspendFunction = context.referenceNativeSuspendFunction()
    private val nativeFlowFunction = context.referenceNativeFlowFunction()

    private fun createNativeBody(declaration: IrFunction, originalFunction: IrSimpleFunction): IrBlockBody {
        val originalReturnType = originalFunction.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalFunction.returnType}")
        return DeclarationIrBuilder(context, declaration.symbol,
            originalFunction.startOffset, originalFunction.endOffset).irBlockBody {
            // Call original function
            var returnType = originalReturnType
            var call: IrExpression = callOriginalFunction(declaration, originalFunction)
            // Call nativeCoroutineScope and create propagatedExceptions array
            val nativeCoroutineScope = callNativeCoroutineScope(declaration)
            val propagatedExceptions = createPropagatedExceptionsArray(originalFunction)
            // Convert Flow types to NativeFlow
            val flowValueType = returnType.getFlowValueTypeOrNull()
            if (flowValueType != null) {
                val valueType = flowValueType.typeOrNull
                    ?: throw IllegalStateException("Unsupported Flow value type $flowValueType")
                returnType = IrSimpleTypeImpl(
                    nativeFlowFunction.owner.returnType.classifierOrFail,
                    false,
                    listOf(flowValueType),
                    emptyList()
                )
                call = irCall(nativeFlowFunction, returnType).apply {
                    putTypeArgument(0, valueType)
                    putValueArgument(0, nativeCoroutineScope)
                    putValueArgument(1, propagatedExceptions)
                    extensionReceiver = call
                }
            }
            // Convert suspend functions to NativeSuspend
            if (originalFunction.isSuspend) {
                val lambdaType = IrSimpleTypeImpl(
                    nativeSuspendFunction.owner.valueParameters[1].type.classifierOrFail,
                    false,
                    listOf(returnType),
                    emptyList()
                )
                val lambda = irLambda(true, lambdaType, returnType) {
                    +irReturn(call)
                }
                returnType = IrSimpleTypeImpl(
                    nativeSuspendFunction.owner.returnType.classifierOrFail,
                    false,
                    listOf(returnType),
                    emptyList()
                )
                call = irCall(nativeSuspendFunction, returnType).apply {
                    putTypeArgument(0, lambda.function.returnType)
                    putValueArgument(0, nativeCoroutineScope)
                    putValueArgument(1, propagatedExceptions)
                    putValueArgument(2, lambda)
                }
            }
            +irReturn(call)
        }
    }

    private fun IrBuilderWithScope.callOriginalFunction(function: IrFunction, originalFunction: IrFunction) =
        irCall(originalFunction).apply {
            dispatchReceiver = function.dispatchReceiverParameter?.let { irGet(it) }
            extensionReceiver = function.extensionReceiverParameter?.let { irGet(it) }
            passTypeArgumentsFrom(function)
            function.valueParameters.forEachIndexed { index, parameter ->
                putValueArgument(index, irGet(parameter))
            }
        }

    private fun IrBuilderWithScope.callNativeCoroutineScope(function: IrFunction): IrExpression {
        val parentClass = function.parentClassOrNull ?: return irNull()
        val nativeCoroutineScopeProperty = parentClass.declarations
            .mapNotNull { it as? IrProperty }
            .firstOrNull { it.isNativeCoroutineScope } ?: return irNull()
        val getter = nativeCoroutineScopeProperty.getter ?: return irNull()
        if (getter.extensionReceiverParameter != null)
            throw UnsupportedOperationException("NativeCoroutineScope property can't be an extension property")
        return irCall(getter).apply {
            dispatchReceiver = function.dispatchReceiverParameter?.let { irGet(it) }
        }
    }

    private val propagatedExceptionClasses = propagatedExceptions.map {
        context.referenceClass(it) ?: throw NoSuchElementException("Couldn't find $it symbol")
    }
    private val propagatedExceptionsArrayElementType = context.irBuiltIns.kClassClass.typeWithArguments(listOf(
        makeTypeProjection(context.irBuiltIns.throwableType, Variance.OUT_VARIANCE)
    ))

    private fun IrBuilderWithScope.createPropagatedExceptionsArray(originalDeclaration: IrDeclaration): IrExpression {
        // Find the annotation on the declaration (or a parent class)
        fun IrDeclaration.getAnnotation(): IrConstructorCall? =
            annotations.findNativeCoroutineThrowsAnnotation() ?:
            useThrowsAnnotation.ifTrue { originalDeclaration.annotations.findThrowsAnnotation() } ?:
            parentClassOrNull?.getAnnotation()
        val annotation = originalDeclaration.getAnnotation()
        // Combine the propagatedExceptionClasses list with the classes from the annotation
        val propagatedExceptions = buildList {
            propagatedExceptionClasses.forEach {
                add(IrClassReferenceImpl(startOffset, endOffset, propagatedExceptionsArrayElementType, it, it.defaultType))
            }
            annotation?.getValueArgument(0)?.let { vararg ->
                if (vararg !is IrVararg) throw IllegalArgumentException("Unexpected vararg: $vararg")
                addAll(vararg.elements)
            }
        }
        return irArrayOf(propagatedExceptionsArrayElementType, propagatedExceptions)
    }
}