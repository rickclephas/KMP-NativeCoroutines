package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom
import org.jetbrains.kotlin.ir.util.substitute

internal fun irCallOriginalFunction(
    originalFunction: IrSimpleFunction,
    function: IrSimpleFunction
): IrBlockBodyExpression {
    val typeArgs = function.typeParameters.map { it.defaultType }
    val returnType = originalFunction.returnType.substitute(originalFunction.typeParameters, typeArgs)
    return IrBlockBodyExpression(returnType) {
        irCall(originalFunction.symbol, returnType).apply {
            passTypeArgumentsFrom(function)
            function.parameters.forEachIndexed { index, parameter ->
                arguments[index] = irGet(parameter)
            }
        }
    }
}
