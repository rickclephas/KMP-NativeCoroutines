package com.rickclephas.kmp.nativecoroutines.compiler.ir.utils

import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType

internal class IrBlockBodyExpression(
    val type: IrType,
    private val build: IrBlockBodyBuilder.() -> IrExpression
) {
    companion object {
        fun IrBlockBodyBuilder.irGet(expression: IrBlockBodyExpression): IrExpression = expression.build(this)
    }
}
