package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

internal fun IrBuilderWithScope.irLambda(
    isSuspend: Boolean,
    returnType: IrType,
    lambdaType: IrType,
    body: IrBlockBodyBuilder.() -> Unit
): IrFunctionExpression {
    val function = context.irFactory.buildFun {
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
        function,
        IrStatementOrigin.LAMBDA
    )
}
