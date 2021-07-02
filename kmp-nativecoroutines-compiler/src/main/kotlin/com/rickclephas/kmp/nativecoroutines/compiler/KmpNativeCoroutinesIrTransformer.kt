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
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parentAsClass

internal class KmpNativeCoroutinesIrTransformer(
    private val context: IrPluginContext,
    private val nameGenerator: NameGenerator
): IrElementTransformerVoidWithContext() {

    private val nativeSuspendFunction = context.referenceNativeSuspendFunction()
    private val nativeFlowFunction = context.referenceNativeFlowFunction()

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (!nameGenerator.isNativeName(declaration.name) ||
            !declaration.isNativeCoroutinesFunction ||
            declaration.body != null
        ) return super.visitFunctionNew(declaration)

        val originalName = nameGenerator.createOriginalName(declaration.name)
        val originalFunction = declaration.parentAsClass.functions.single {
            it.isCoroutinesFunction && it.name == originalName && it.valueParameters.areSameAs(declaration.valueParameters)
        }
        val originalReturnType = originalFunction.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalFunction.returnType}")

        declaration.body = DeclarationIrBuilder(context, declaration.symbol).irBlockBody {

            // Call original function
            var returnType = originalReturnType
            var call: IrExpression = irCall(originalFunction).apply {
                dispatchReceiver = declaration.dispatchReceiverParameter?.let { irGet(it) }
                extensionReceiver = declaration.extensionReceiverParameter?.let { irGet(it) }
                declaration.valueParameters.forEachIndexed { index, parameter ->
                    putValueArgument(index, irGet(parameter))
                }
            }

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
        return super.visitFunctionNew(declaration)
    }
}