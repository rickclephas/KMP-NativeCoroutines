package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom

internal fun irCallOriginalPropertyGetter(
    originalGetter: IrSimpleFunction,
    propertyFunction: IrSimpleFunction
): IrBlockBodyExpression = IrBlockBodyExpression(originalGetter.returnType) {
    irCall(originalGetter).apply {
        dispatchReceiver = propertyFunction.dispatchReceiverParameter?.let { irGet(it) }
        extensionReceiver = propertyFunction.extensionReceiverParameter?.let { irGet(it) }
        passTypeArgumentsFrom(propertyFunction)
    }
}
