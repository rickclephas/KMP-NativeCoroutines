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
        dispatchReceiver = function.dispatchReceiverParameter?.let { irGet(it) }
        extensionReceiver = function.extensionReceiverParameter?.let { irGet(it) }
        passTypeArgumentsFrom(function)
        function.valueParameters.forEachIndexed { index, parameter ->
            putValueArgument(index, irGet(parameter))
        }
    }
}
