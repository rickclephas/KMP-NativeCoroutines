package com.rickclephas.kmp.nativecoroutines.compiler.ir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.NativeCoroutinesDeclarationKey
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.*
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.GeneratorContext
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.buildNativeFunctionBody
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.buildStateFlowValueGetterBody
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
                originalFunction = function.owner,
                callableSignature = declarationKey.callableSignature
            )
            else -> error("Unsupported type $type for native coroutines function")
        }
    }

    override fun visitProperty(declaration: IrProperty) {
        val declarationKey = declaration.nativeCoroutinesDeclarationKey ?: return
        // TODO: support non-unique callableIds
        val property = pluginContext.referenceProperties(declarationKey.callableSignature.callableId).single()
        val getter = declaration.getter
        if (getter != null) {
            require(getter.body == null)
            getter.body = when (declarationKey.type) {
                NativeCoroutinesDeclarationKey.Type.NATIVE -> TODO()
                NativeCoroutinesDeclarationKey.Type.STATE_FLOW_VALUE -> context.buildStateFlowValueGetterBody(
                    property = declaration,
                    originalProperty = property.owner
                )
                NativeCoroutinesDeclarationKey.Type.SHARED_FLOW_REPLAY_CACHE -> TODO()
            }
        }
        val setter = declaration.setter
        if (setter != null) {
            require(setter.body == null)
            setter.body = when (val type = declarationKey.type) {
                NativeCoroutinesDeclarationKey.Type.STATE_FLOW_VALUE -> context.buildStateFlowValueSetterBody(
                    property = declaration,
                    originalProperty = property.owner
                )
                else -> error("Unsupported type $type for native coroutines property setter")
            }
        }
    }
}
