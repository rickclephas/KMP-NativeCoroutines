package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.getFlowValueTypeArg
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.substitute

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallAsNativeFlow(
    flowExpression: IrBlockBodyExpression,
    coroutineScope: IrVariable
): IrBlockBodyExpression {
    val context = context as GeneratorContext
    val flowType = flowExpression.type
    val valueTypeArg = flowType.getFlowValueTypeArg()
    val nativeFlowType = context.asNativeFlowSymbol.owner.run {
        returnType.substitute(typeParameters, listOf(valueTypeArg.typeOrFail))
    }
    val returnType = if (flowType.isNullable()) nativeFlowType.makeNullable() else nativeFlowType
    return IrBlockBodyExpression(returnType) {
        val flow = irTemporary(irGet(flowExpression))
        var expression: IrExpression = irCall(context.asNativeFlowSymbol, nativeFlowType).apply {
            putTypeArgument(0, valueTypeArg.typeOrFail)
            extensionReceiver = irGet(flow)
            putValueArgument(0, irGet(coroutineScope))
        }
        if (flowType.isNullable()) {
            expression = irIfNull(returnType, irGet(flow), irNull(returnType), expression)
        }
        expression
    }
}
