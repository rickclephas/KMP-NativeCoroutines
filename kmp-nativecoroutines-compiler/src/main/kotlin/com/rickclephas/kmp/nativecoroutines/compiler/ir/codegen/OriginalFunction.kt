package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom

internal fun IrBuilderWithScope.irCallOriginalFunction(
    originalFunction: IrSimpleFunction,
    function: IrSimpleFunction
): IrFunctionAccessExpression = irCall(originalFunction).apply {
    dispatchReceiver = function.dispatchReceiverParameter?.let { irGet(it) }
    extensionReceiver = function.extensionReceiverParameter?.let { irGet(it) }
    passTypeArgumentsFrom(function)
    function.valueParameters.forEachIndexed { index, parameter ->
        putValueArgument(index, irGet(parameter))
    }
}
