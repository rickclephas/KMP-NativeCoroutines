package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.getFlowValueTypeArg
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildSharedFlowReplayCacheGetterBody(
    property: IrProperty,
    originalProperty: IrProperty
): IrBlockBody {
    val getter = property.getter
    require(getter != null)
    return DeclarationIrBuilder(
        generatorContext = this,
        symbol = getter.symbol,
    ).irBlockBody {
        var expression = irGet(irCallOriginalPropertyGetter(originalProperty, getter))
        val flowType = expression.type
        val valueTypeArg = flowType.getFlowValueTypeArg()
        val valueType = valueTypeArg.typeOrFail
        val returnType = irBuiltIns.listClass.createType(
            flowType.isNullable(),
            listOf(valueTypeArg)
        )
        val replayCacheGetter = sharedFlowReplayCacheSymbol.owner.getter?.symbol
            ?: error("Failed to find SharedFlow.replayCache getter")
        val flow = irTemporary(expression)
        expression = irCall(replayCacheGetter, valueType).apply {
            dispatchReceiver = irGet(flow)
        }
        if (flowType.isNullable()) {
            expression = irIfNull(returnType, irGet(flow), irNull(returnType), expression)
        }
        +irReturn(expression)
    }
}
