package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.getFlowValueTypeArg
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallAsNativeFlow(
    flowExpression: IrExpression,
    coroutineScope: IrVariable
): IrCall {
    val context = context as GeneratorContext
    val valueTypeArg = flowExpression.type.getFlowValueTypeArg()
    val returnType = IrSimpleTypeImpl(
        context.asNativeFlowSymbol.owner.returnType.classifierOrFail,
        false,
        listOf(valueTypeArg),
        emptyList()
    )
    return irCall(context.asNativeFlowSymbol, returnType).apply {
        putTypeArgument(0, valueTypeArg.typeOrFail)
        extensionReceiver = flowExpression
        putValueArgument(0, irGet(coroutineScope))
    }
}
