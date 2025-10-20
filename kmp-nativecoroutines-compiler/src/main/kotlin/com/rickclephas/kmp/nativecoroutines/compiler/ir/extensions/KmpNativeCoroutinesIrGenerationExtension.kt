package com.rickclephas.kmp.nativecoroutines.compiler.ir.extensions

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid

internal class KmpNativeCoroutinesIrGenerationExtension: IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
//        moduleFragment.acceptVoid(KmpNativeCoroutinesIrTransformer(pluginContext))
    }
}
