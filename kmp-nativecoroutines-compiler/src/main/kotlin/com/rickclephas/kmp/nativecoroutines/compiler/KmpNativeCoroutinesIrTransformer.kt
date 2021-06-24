package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name

internal class KmpNativeCoroutinesIrTransformer(
    private val context: IrPluginContext,
    private val suffix: String
): IrElementTransformerVoidWithContext() {

    private val nativeSuspendFun = context.referenceFunctions(nativeSuspendFuncFqName).single {
        it.owner.valueParameters.size == 2
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val nativeSuspendType = context.referenceTypeAlias(nativeSuspendFqName)?.owner
            ?: throw IllegalStateException("Couldn't find NativeSuspend typealias")

        val nativeName = declaration.name
        val nativeReturnType = declaration.returnType as? IrSimpleType
        if (nativeName.isSpecial || !nativeName.identifier.endsWith(suffix) || declaration.body != null ||
            nativeReturnType?.abbreviation?.typeAlias?.owner != nativeSuspendType)
            return super.visitFunctionNew(declaration)

        val originalName = Name.identifier(nativeName.identifier.removeSuffix(suffix))
        val originalFunction = declaration.parentAsClass.functions.single {
            it.isSuspend && it.name == originalName && it.valueParameters.size == declaration.valueParameters.size
            // TODO: Validate actual value parameters instead of the count
        }
        val originalReturnType = originalFunction.returnType as? IrSimpleTypeImpl
            ?: throw IllegalStateException("Unsupported return type ${originalFunction.returnType}")

        declaration.body = DeclarationIrBuilder(context, declaration.symbol, declaration.startOffset, declaration.endOffset).irBlockBody {
            val blockDescriptor = context.irFactory.buildFun {
                name = Name.special("<anonymous>")
                visibility = DescriptorVisibilities.LOCAL
                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                isSuspend = true
                returnType = originalReturnType
            }.apply {
                parent = declaration
                body = DeclarationIrBuilder(context, symbol, startOffset, endOffset).irBlockBody {
                    val functionCall = irCall(originalFunction)
                    val dispatchReceiverParameter = declaration.dispatchReceiverParameter
                    if (dispatchReceiverParameter != null)
                        functionCall.dispatchReceiver = irGet(dispatchReceiverParameter)
                    val extensionReceiverParameter = declaration.extensionReceiverParameter
                    if (extensionReceiverParameter != null)
                        functionCall.extensionReceiver = irGet(extensionReceiverParameter)
                    declaration.valueParameters.forEachIndexed { index, parameter ->
                        functionCall.putValueArgument(index, irGet(parameter))
                    }
                    +irReturn(functionCall)
                }
            }
            val blockType = IrSimpleTypeImpl(
                nativeSuspendFun.owner.valueParameters[1].type.classifierOrFail,
                false,
                listOf(originalReturnType),
                emptyList()
            )
            val blockExpression = IrFunctionExpressionImpl(
                declaration.startOffset,
                declaration.endOffset,
                blockType,
                blockDescriptor,
                IrStatementOrigin.LAMBDA
            )
            val callNativeSuspend = irCall(nativeSuspendFun, nativeReturnType)
            callNativeSuspend.putTypeArgument(0, originalReturnType)
            callNativeSuspend.putValueArgument(1, blockExpression)
            +irReturn(callNativeSuspend)
        }
        return super.visitFunctionNew(declaration)
    }
}