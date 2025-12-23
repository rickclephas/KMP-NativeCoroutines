package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.config.SwiftExport
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildNativeFunctionBody(
    function: IrSimpleFunction,
    originalFunction: IrSimpleFunction,
    callableSignature: CallableSignature,
    swiftExport: Set<SwiftExport>,
): IrBlockBody = DeclarationIrBuilder(
    generatorContext = this,
    symbol = function.symbol,
).irBlockBody {
    val coroutineScope = irTemporary(irCallCoroutineScope(originalFunction, function))
    var expression = irCallOriginalFunction(originalFunction, function)
    if (SwiftExport.NO_FUNC_RETURN_TYPES !in swiftExport) {
        if (callableSignature.returnType is CallableSignature.Type.Flow) {
            expression = irCallAsNativeFlow(expression, coroutineScope)
        }
        if (callableSignature.isSuspend) {
            expression = irCallNativeSuspend(expression, coroutineScope)
        }
    }
    +irReturn(irGet(expression))
}
