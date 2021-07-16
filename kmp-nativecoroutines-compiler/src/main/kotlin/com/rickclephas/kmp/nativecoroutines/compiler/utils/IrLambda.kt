package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.name.Name

internal fun IrBuilderWithScope.irLambda(
    isSuspend: Boolean,
    lambdaType: IrSimpleTypeImpl,
    returnType: IrSimpleTypeImpl,
    body: IrBlockBodyBuilder.() -> Unit
): IrFunctionExpression {
    val lambdaFunction = context.irFactory.buildFun {
        startOffset = this@irLambda.startOffset
        endOffset = this@irLambda.endOffset
        name = Name.special("<anonymous>")
        visibility = DescriptorVisibilities.LOCAL
        origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
        this.isSuspend = isSuspend
        this.returnType = returnType
    }.apply {
        parent = this@irLambda.parent
        this.body = DeclarationIrBuilder(context, symbol).irBlockBody(body = body)
    }
    return IrFunctionExpressionImpl(
        startOffset,
        endOffset,
        lambdaType,
        lambdaFunction,
        IrStatementOrigin.LAMBDA
    )
}