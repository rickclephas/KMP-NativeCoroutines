package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom

internal fun IrBuilderWithScope.irCallOriginalPropertyGetter(
    originalProperty: IrProperty,
    propertyFunction: IrSimpleFunction
): IrFunctionAccessExpression {
    val originalGetter = originalProperty.getter
    require(originalGetter != null)
    return irCall(originalGetter).apply {
        dispatchReceiver = propertyFunction.dispatchReceiverParameter?.let { irGet(it) }
        extensionReceiver = propertyFunction.extensionReceiverParameter?.let { irGet(it) }
        passTypeArgumentsFrom(propertyFunction)
    }
}
