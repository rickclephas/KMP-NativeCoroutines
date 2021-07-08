package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.utils.*
import com.rickclephas.kmp.nativecoroutines.compiler.utils.isNativeCoroutinesFunction
import com.rickclephas.kmp.nativecoroutines.compiler.utils.referenceNativeSuspendFunction
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties

internal class KmpNativeCoroutinesIrTransformer(
    private val context: IrPluginContext,
    private val nameGenerator: NameGenerator
): IrElementTransformerVoidWithContext() {

    private val nativeSuspendFunction = context.referenceNativeSuspendFunction()
    private val nativeFlowFunction = context.referenceNativeFlowFunction()
    private val stateFlowValueProperty = context.referenceStateFlowValueProperty()

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
        if (declaration.getter?.body != null || declaration.setter != null)
            return super.visitPropertyNew(declaration)

        val isNativeName = nameGenerator.isNativeName(declaration.name)
        val isNativeValueName = nameGenerator.isNativeValueName(declaration.name)
        if (!isNativeName && !isNativeValueName) return super.visitPropertyNew(declaration)

        val getter = declaration.getter ?: return super.visitPropertyNew(declaration)
        val originalName = nameGenerator.createOriginalName(declaration.name)
        val originalProperty = declaration.parentAsClass.properties.single {
            it.isCoroutinesProperty && it.name == originalName
        }
        val originalGetter = originalProperty.getter
            ?: throw IllegalStateException("Original property doesn't have a getter")

        getter.body = when {
            isNativeName -> createNativeBody(getter, originalGetter)
            isNativeValueName -> createNativeValueBody(getter, originalGetter)
            else -> throw IllegalStateException("Unsupported property type")
        }
        return super.visitPropertyNew(declaration)
    }

    private fun createNativeValueBody(getter: IrFunction, originalGetter: IrSimpleFunction): IrBlockBody {
        val originalReturnType = originalGetter.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalGetter.returnType}")
        return DeclarationIrBuilder(context, getter.symbol).irBlockBody {
            val returnType = originalReturnType.getStateFlowValueTypeOrNull()?.typeOrNull
                ?: throw IllegalStateException("Unsupported StateFlow value type $originalReturnType")
            val valueGetter = stateFlowValueProperty.owner.getter?.symbol
                ?: throw IllegalStateException("Couldn't find StateFlow value getter")
            +irReturn(irCall(valueGetter, returnType).apply {
                dispatchReceiver = callOriginalFunction(getter, originalGetter)
            })
        }
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (!nameGenerator.isNativeName(declaration.name) ||
            !declaration.isNativeCoroutinesFunction ||
            declaration.body != null
        ) return super.visitFunctionNew(declaration)
        val originalName = nameGenerator.createOriginalName(declaration.name)
        val originalFunction = declaration.parentAsClass.functions.single {
            it.isCoroutinesFunction && it.name == originalName && it.valueParameters.areSameAs(declaration.valueParameters)
        }
        declaration.body = createNativeBody(declaration, originalFunction)
        return super.visitFunctionNew(declaration)
    }

    private fun createNativeBody(declaration: IrFunction, originalFunction: IrSimpleFunction): IrBlockBody {
        val originalReturnType = originalFunction.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalFunction.returnType}")
        return DeclarationIrBuilder(context, declaration.symbol).irBlockBody {
            // Call original function
            var returnType = originalReturnType
            var call: IrExpression = callOriginalFunction(declaration, originalFunction)
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
                    putValueArgument(1, lambda)
                }
            }
            +irReturn(call)
        }
    }

    private fun IrBuilderWithScope.callOriginalFunction(function: IrFunction, originalFunction: IrFunction) =
        irCall(originalFunction).apply {
            dispatchReceiver = function.dispatchReceiverParameter?.let { irGet(it) }
            extensionReceiver = function.extensionReceiverParameter?.let { irGet(it) }
            function.valueParameters.forEachIndexed { index, parameter ->
                putValueArgument(index, irGet(parameter))
            }
        }
}