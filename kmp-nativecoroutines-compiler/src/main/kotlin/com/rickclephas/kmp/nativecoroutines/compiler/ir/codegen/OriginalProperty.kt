package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom

internal fun irCallOriginalPropertyGetter(
    originalGetter: IrSimpleFunction,
    propertyFunction: IrSimpleFunction
): IrBlockBodyExpression = IrBlockBodyExpression(originalGetter.returnType) {
    irCall(originalGetter).apply {
        propertyFunction.parameters.filter { it.kind != IrParameterKind.Regular }.forEachIndexed { index, parameter ->
            arguments[index] = irGet(parameter)
        }
        passTypeArgumentsFrom(propertyFunction)
    }
}
