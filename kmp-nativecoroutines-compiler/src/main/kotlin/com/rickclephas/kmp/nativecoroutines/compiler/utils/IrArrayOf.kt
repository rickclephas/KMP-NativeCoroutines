package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrVarargElement
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith

internal fun IrBuilderWithScope.irArrayOf(
    elementType: IrType,
    elements: List<IrVarargElement>
): IrExpression {
    val arrayType = context.irBuiltIns.arrayClass.typeWith(elementType)
    val vararg = IrVarargImpl(startOffset, endOffset, arrayType, elementType, elements)
    return irCall(context.irBuiltIns.arrayOf, arrayType, listOf(elementType)).apply {
        putValueArgument(0, vararg)
    }
}