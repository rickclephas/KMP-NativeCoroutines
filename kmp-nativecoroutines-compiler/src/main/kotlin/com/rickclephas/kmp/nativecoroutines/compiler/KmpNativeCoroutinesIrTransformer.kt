package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction

internal class KmpNativeCoroutinesIrTransformer(
    private val context: IrPluginContext,
    private val suffix: String
): IrElementTransformerVoidWithContext() {

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.name.isSpecial || !declaration.name.identifier.endsWith(suffix) ||
            declaration.returnType != context.irBuiltIns.stringType || declaration.body != null)
            return super.visitFunctionNew(declaration)
        declaration.body = DeclarationIrBuilder(context, declaration.symbol, declaration.startOffset, declaration.endOffset).irBlockBody {
            +irReturn(irString("Hello KMP-NativeCoroutines codegen!"))
        }
        return super.visitFunctionNew(declaration)
    }
}