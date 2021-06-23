package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.*

internal class KmpNativeCoroutinesIrGenerationExtension(
    private val suffix: String
): IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.accept(KmpNativeCoroutinesIrTransformer(pluginContext, suffix), null)
    }
}

