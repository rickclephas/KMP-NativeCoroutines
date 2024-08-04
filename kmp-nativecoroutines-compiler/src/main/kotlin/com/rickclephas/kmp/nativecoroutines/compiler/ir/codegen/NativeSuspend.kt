package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.util.substitute

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallNativeSuspend(
    blockExpression: IrBlockBodyExpression,
    coroutineScope: IrVariable,
): IrBlockBodyExpression {
    val context = context as GeneratorContext
    val expressionType = blockExpression.type as IrSimpleType
    val isUnit = expressionType.isUnit()
    val nativeSuspendSymbol = when (isUnit) {
        true -> context.nativeSuspendUnitSymbol
        false -> context.nativeSuspendSymbol
    }
    val lambdaType = nativeSuspendSymbol.owner.run {
        valueParameters[1].type.substitute(typeParameters, listOf(expressionType))
    }
    val returnType = nativeSuspendSymbol.owner.run {
        if (isUnit) return@run returnType
        returnType.substitute(typeParameters, listOf(expressionType))
    }
    return IrBlockBodyExpression(returnType) {
        val lambda = irLambda(true, expressionType, lambdaType) {
            +irReturn(irGet(blockExpression))
        }
        irCall(nativeSuspendSymbol, returnType).apply {
            if (!isUnit) putTypeArgument(0, expressionType)
            putValueArgument(0, irGet(coroutineScope))
            putValueArgument(1, lambda)
        }
    }
}
