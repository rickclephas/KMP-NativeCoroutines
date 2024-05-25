package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildNativeFunctionBody(
    function: IrSimpleFunction,
    originalSymbol: IrSimpleFunctionSymbol,
    callableSignature: CallableSignature
): IrBlockBody = DeclarationIrBuilder(
    generatorContext = this,
    symbol = function.symbol,
    startOffset = originalSymbol.owner.startOffset,
    endOffset = originalSymbol.owner.endOffset
).irBlockBody {
    val coroutineScope = irTemporary(irCallCoroutineScope(function))
    var expression = irCallOriginalFunction(originalSymbol, function)
    if (callableSignature.returnType is CallableSignature.Type.Flow) {
        expression = irCallAsNativeFlow(expression, coroutineScope)
    }
    if (callableSignature.isSuspend) {
        expression = irCallNativeSuspend(expression, coroutineScope)
    }
    +irReturn(expression)
}

internal fun IrBuilderWithScope.irCallOriginalFunction(
    originalSymbol: IrSimpleFunctionSymbol,
    function: IrFunction
): IrCall = irCall(originalSymbol).apply {
    dispatchReceiver = function.dispatchReceiverParameter?.let { irGet(it) }
    extensionReceiver = function.extensionReceiverParameter?.let { irGet(it) }
    passTypeArgumentsFrom(function)
    function.valueParameters.forEachIndexed { index, parameter ->
        putValueArgument(index, irGet(parameter))
    }
}
