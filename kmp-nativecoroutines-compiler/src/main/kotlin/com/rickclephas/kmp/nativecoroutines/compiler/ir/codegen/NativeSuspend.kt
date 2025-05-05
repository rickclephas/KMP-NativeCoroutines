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
import org.jetbrains.kotlin.ir.util.substitute

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallNativeSuspend(
    blockExpression: IrBlockBodyExpression,
    coroutineScope: IrVariable,
): IrBlockBodyExpression {
    val context = context as GeneratorContext
    val expressionType = blockExpression.type as IrSimpleType
    val lambdaType = context.nativeSuspendSymbol.owner.run {
        parameters[1].type.substitute(typeParameters, listOf(expressionType))
    }
    val returnType = context.nativeSuspendSymbol.owner.run {
        returnType.substitute(typeParameters, listOf(expressionType))
    }
    return IrBlockBodyExpression(returnType) {
        val lambda = irLambda(true, expressionType, lambdaType) {
            +irReturn(irGet(blockExpression))
        }
        irCall(context.nativeSuspendSymbol, returnType).apply {
            typeArguments[0] = expressionType
            arguments[0] = irGet(coroutineScope)
            arguments[1] = lambda
        }
    }
}
