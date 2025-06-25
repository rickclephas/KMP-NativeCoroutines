package com.rickclephas.kmp.nativecoroutines.compiler.ir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.NativeCoroutinesDeclarationKey
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.*
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.GeneratorContext
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.buildNativeFunctionBody
import com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen.buildStateFlowValueGetterBody
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCallableReference
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal class KmpNativeCoroutinesIrTransformer(
    pluginContext: IrPluginContext,
): IrVisitorVoid() {

    private val context = GeneratorContext(pluginContext)

    private val IrDeclaration.nativeCoroutinesDeclarationKey: NativeCoroutinesDeclarationKey? get() {
        val origin = origin
        if (origin !is IrDeclarationOrigin.GeneratedByPlugin) return null
        return origin.pluginKey as? NativeCoroutinesDeclarationKey
    }

    private fun <T: IrSymbol> IrSimpleFunction.getOriginalSymbol(): T {
        val body = body
        requireNotNull(body)
        val statement = body.statements.first() as IrTypeOperatorCall
        val callableReference = statement.argument as IrCallableReference<*>
        @Suppress("UNCHECKED_CAST")
        return callableReference.symbol as T
    }

    override fun visitElement(element: IrElement) = when (element) {
        is IrModuleFragment,
        is IrFile,
        is IrClass -> element.acceptChildrenVoid(this)
        else -> {}
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        val declarationKey = declaration.nativeCoroutinesDeclarationKey ?: return
        val function = declaration.getOriginalSymbol<IrSimpleFunctionSymbol>()
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
        val getter = declaration.getter
        requireNotNull(getter)
        val property = getter.getOriginalSymbol<IrPropertySymbol>()
        getter.body = when (declarationKey.type) {
            NativeCoroutinesDeclarationKey.Type.NATIVE -> context.buildNativePropertyGetterBody(
                function = getter,
                originalProperty = property.owner,
                callableSignature = declarationKey.callableSignature
            )
            NativeCoroutinesDeclarationKey.Type.STATE_FLOW_VALUE -> context.buildStateFlowValueGetterBody(
                property = declaration,
                originalProperty = property.owner
            )
            NativeCoroutinesDeclarationKey.Type.SHARED_FLOW_REPLAY_CACHE -> context.buildSharedFlowReplayCacheGetterBody(
                property = declaration,
                originalProperty = property.owner
            )
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
