package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom

internal fun irCallOriginalFunction(
    originalFunction: IrSimpleFunction,
    function: IrSimpleFunction
) = IrBlockBodyExpression(originalFunction.returnType) {
    irCall(originalFunction).apply {
        passTypeArgumentsFrom(function)
        function.parameters.forEachIndexed { index, parameter ->
            arguments[index] = irGet(parameter)
        }
    }
}
