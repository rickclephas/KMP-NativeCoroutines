package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallNativeSuspend(
    blockExpression: IrExpression,
    coroutineScope: IrVariable,
): IrCall {
    val context = context as GeneratorContext
    val expressionType = blockExpression.type as IrSimpleType
    val lambdaType = IrSimpleTypeImpl(
        context.nativeSuspendSymbol.owner.valueParameters[1].type.classifierOrFail,
        false,
        listOf(expressionType),
        emptyList()
    )
    val lambda = irLambda(true, expressionType, lambdaType) {
        +irReturn(blockExpression)
    }
    val returnType = IrSimpleTypeImpl(
        context.nativeSuspendSymbol.owner.returnType.classifierOrFail,
        false,
        listOf(expressionType),
        emptyList()
    )
    return irCall(context.nativeSuspendSymbol, returnType).apply {
        putTypeArgument(0, expressionType)
        putValueArgument(0, irGet(coroutineScope))
        putValueArgument(1, lambda)
    }
}
