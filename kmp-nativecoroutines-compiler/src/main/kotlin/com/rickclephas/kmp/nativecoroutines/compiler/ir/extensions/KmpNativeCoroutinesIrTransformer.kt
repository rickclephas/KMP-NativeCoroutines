package com.rickclephas.kmp.nativecoroutines.compiler.ir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.NativeCoroutinesDeclarationKey
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.GeneratorContext
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.buildNativeFunctionBody
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal class KmpNativeCoroutinesIrTransformer(
    private val pluginContext: IrPluginContext,
): IrElementVisitorVoid {

    private val context = GeneratorContext(pluginContext)

    private val IrDeclaration.nativeCoroutinesDeclarationKey: NativeCoroutinesDeclarationKey? get() {
        val origin = origin
        if (origin !is IrDeclarationOrigin.GeneratedByPlugin) return null
        return origin.pluginKey as? NativeCoroutinesDeclarationKey
    }

    override fun visitElement(element: IrElement) = when (element) {
        is IrModuleFragment,
        is IrFile,
        is IrClass -> element.acceptChildrenVoid(this)
        else -> {}
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        val declarationKey = declaration.nativeCoroutinesDeclarationKey ?: return
        require(declaration.body == null)
        // TODO: support non-unique callableIds
        val function = pluginContext.referenceFunctions(declarationKey.callableSignature.callableId).single()
        declaration.body = when (val type = declarationKey.type) {
            NativeCoroutinesDeclarationKey.Type.NATIVE -> context.buildNativeFunctionBody(
                function = declaration,
                originalSymbol = function,
                callableSignature = declarationKey.callableSignature
            )
            else -> error("Unsupported type $type for native coroutines function")
        }
    }

    override fun visitProperty(declaration: IrProperty) {
        val declarationKey = declaration.nativeCoroutinesDeclarationKey ?: return
        require(declaration.getter == null)
        require(declaration.setter == null)
        TODO("Add property body")
    }
}
