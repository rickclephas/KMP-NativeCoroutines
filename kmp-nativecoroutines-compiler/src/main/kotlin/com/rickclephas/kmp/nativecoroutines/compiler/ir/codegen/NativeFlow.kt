package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.getFlowValueTypeArg
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.substitute

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallAsNativeFlow(
    flowExpression: IrBlockBodyExpression,
    coroutineScope: IrVariable
): IrBlockBodyExpression {
    val context = context as GeneratorContext
    val flowType = flowExpression.type
    val valueType = flowType.getFlowValueTypeArg().typeOrFail
    val isUnit = valueType.isUnit()
    val asNativeFlowSymbol = when (isUnit) {
        true -> context.asNativeFlowUnitSymbol
        false -> context.asNativeFlowSymbol
    }
    val nativeFlowType = asNativeFlowSymbol.owner.run {
        if (isUnit) return@run returnType
        returnType.substitute(typeParameters, listOf(valueType))
    }
    val returnType = if (flowType.isNullable()) nativeFlowType.makeNullable() else nativeFlowType
    return IrBlockBodyExpression(returnType) {
        val flow = irTemporary(irGet(flowExpression))
        var expression: IrExpression = irCall(asNativeFlowSymbol, nativeFlowType).apply {
            if (!isUnit) putTypeArgument(0, valueType)
            extensionReceiver = irGet(flow)
            putValueArgument(0, irGet(coroutineScope))
        }
        if (flowType.isNullable()) {
            expression = irIfNull(returnType, irGet(flow), irNull(returnType), expression)
        }
        expression
    }
}
