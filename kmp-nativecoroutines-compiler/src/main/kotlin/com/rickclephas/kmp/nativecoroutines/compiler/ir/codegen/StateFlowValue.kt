package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.getFlowValueTypeArg
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.isNullable

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildStateFlowValueGetterBody(
    property: IrProperty,
    originalProperty: IrProperty
): IrBlockBody {
    val getter = property.getter
    require(getter != null)
    val originalGetter = originalProperty.getter
    require(originalGetter != null)
    return DeclarationIrBuilder(
        generatorContext = this,
        symbol = getter.symbol,
    ).irBlockBody {
        var expression = irGet(irCallOriginalPropertyGetter(originalGetter, getter))
        val flowType = expression.type
        val valueType = flowType.getFlowValueTypeArg().typeOrFail
        val returnType = if (flowType.isNullable()) valueType.makeNullable() else valueType
        val valueGetter = stateFlowValueSymbol.owner.getter?.symbol
            ?: error("Failed to find StateFlow.value getter")
        val flow = irTemporary(expression)
        expression = irCall(valueGetter, valueType).apply {
            arguments[0] = irGet(flow)
        }
        if (flowType.isNullable()) {
            expression = irIfNull(returnType, irGet(flow), irNull(returnType), expression)
        }
        +irReturn(expression)
    }
}

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildStateFlowValueSetterBody(
    property: IrProperty,
    originalProperty: IrProperty
): IrBlockBody {
    val setter = property.setter
    require(setter != null)
    val originalGetter = originalProperty.getter
    require(originalGetter != null)
    return DeclarationIrBuilder(
        generatorContext = this,
        symbol = setter.symbol,
    ).irBlockBody {
        var expression = irGet(irCallOriginalPropertyGetter(originalGetter, setter))
        val valueSetter = mutableStateFlowValueSymbol.owner.setter?.symbol
            ?: error("Failed to find MutableStateFlow.value setter")
        expression = irCall(valueSetter).apply {
            arguments[0] = expression
            arguments[1] = irGet(setter.parameters.first { it.kind == IrParameterKind.Regular })
        }
        +irReturn(expression)
    }
}
