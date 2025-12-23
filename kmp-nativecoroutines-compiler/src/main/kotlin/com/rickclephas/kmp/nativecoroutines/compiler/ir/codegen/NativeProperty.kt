package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.config.SwiftExport
import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression.Companion.irGet
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
    callableSignature: CallableSignature,
    swiftExport: Set<SwiftExport>,
): IrBlockBody = DeclarationIrBuilder(
    generatorContext = this,
    symbol = function.symbol,
).irBlockBody {
    val originalGetter = originalProperty.getter
    require(originalGetter != null)
    val coroutineScope = irTemporary(irCallCoroutineScope(originalGetter, function))
    var expression = irCallOriginalPropertyGetter(originalGetter, function)
    if (SwiftExport.NO_FUNC_RETURN_TYPES !in swiftExport) {
        if (callableSignature.returnType is CallableSignature.Type.Flow) {
            expression = irCallAsNativeFlow(expression, coroutineScope)
        }
    }
    +irReturn(irGet(expression))
}
