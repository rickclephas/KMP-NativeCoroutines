package com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

internal class KmpNativeCoroutinesFirExtensionRegistrar(
    private val configuration: CompilerConfiguration
): FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::KmpNativeCoroutinesFirAdditionalCheckersExtension.bind(configuration)
    }
}
