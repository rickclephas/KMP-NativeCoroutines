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
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallNativeSuspend(
    blockExpression: IrBlockBodyExpression,
    coroutineScope: IrVariable,
): IrBlockBodyExpression {
    val context = context as GeneratorContext
    val expressionType = blockExpression.type as IrSimpleType
    val lambdaType = IrSimpleTypeImpl(
        context.nativeSuspendSymbol.owner.valueParameters[1].type.classifierOrFail,
        false,
        listOf(expressionType),
        emptyList()
    )
    val returnType = IrSimpleTypeImpl(
        context.nativeSuspendSymbol.owner.returnType.classifierOrFail,
        false,
        listOf(expressionType),
        emptyList()
    )
    return IrBlockBodyExpression(returnType) {
        val lambda = irLambda(true, expressionType, lambdaType) {
            +irReturn(irGet(blockExpression))
        }
        irCall(context.nativeSuspendSymbol, returnType).apply {
            putTypeArgument(0, expressionType)
            putValueArgument(0, irGet(coroutineScope))
            putValueArgument(1, lambda)
        }
    }
}
