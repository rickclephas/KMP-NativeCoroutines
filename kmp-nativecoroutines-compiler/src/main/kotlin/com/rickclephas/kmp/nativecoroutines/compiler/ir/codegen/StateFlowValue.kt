package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.getFlowValueTypeArg
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.typeOrFail

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildStateFlowValueGetterBody(
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
        val valueTypeArg = expression.type.getFlowValueTypeArg()
        val valueGetter = stateFlowValueSymbol.owner.getter?.symbol
            ?: error("Failed to find StateFlow.value getter")
        expression = irCall(valueGetter, valueTypeArg.typeOrFail).apply {
            dispatchReceiver = expression
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
    return DeclarationIrBuilder(
        generatorContext = this,
        symbol = setter.symbol,
    ).irBlockBody {
        var expression = irGet(irCallOriginalPropertyGetter(originalProperty, setter))
        val valueSetter = mutableStateFlowValueSymbol.owner.setter?.symbol
            ?: error("Failed to find MutableStateFlow.value setter")
        expression = irCall(valueSetter).apply {
            dispatchReceiver = expression
            putValueArgument(0, irGet(setter.valueParameters.first()))
        }
        +irReturn(expression)
    }
}
