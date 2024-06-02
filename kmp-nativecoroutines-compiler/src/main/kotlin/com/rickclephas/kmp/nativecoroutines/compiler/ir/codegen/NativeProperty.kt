package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

@UnsafeDuringIrConstructionAPI
internal fun GeneratorContext.buildNativePropertyGetterBody(
    function: IrSimpleFunction,
    originalProperty: IrProperty,
    callableSignature: CallableSignature
): IrBlockBody = DeclarationIrBuilder(
    generatorContext = this,
    symbol = function.symbol,
    startOffset = originalProperty.startOffset,
    endOffset = originalProperty.endOffset
).irBlockBody {
    val coroutineScope = irTemporary(irCallCoroutineScope(function))
    var expression = irCallOriginalPropertyGetter(originalProperty, function)
    if (callableSignature.returnType is CallableSignature.Type.Flow) {
        expression = irCallAsNativeFlow(expression, coroutineScope)
    }
    +irReturn(expression)
}
