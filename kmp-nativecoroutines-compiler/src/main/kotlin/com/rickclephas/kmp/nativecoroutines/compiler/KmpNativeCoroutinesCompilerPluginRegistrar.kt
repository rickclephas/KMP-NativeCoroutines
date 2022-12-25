package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

@OptIn(ExperimentalCompilerApi::class)
class KmpNativeCoroutinesCompilerPluginRegistrar: CompilerPluginRegistrar() {

    override val supportsK2: Boolean = false // TODO: Add K2 support

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        StorageComponentContainerContributor.registerExtension(KmpNativeCoroutinesStorageComponentContainerContributor())
    }
}
