package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.extensions.KmpNativeCoroutinesStorageComponentContainerContributor
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

@OptIn(ExperimentalCompilerApi::class)
public class KmpNativeCoroutinesCompilerPluginRegistrar: CompilerPluginRegistrar() {

    override val supportsK2: Boolean = false // TODO: Add K2 support

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        StorageComponentContainerContributor.registerExtension(
            KmpNativeCoroutinesStorageComponentContainerContributor(configuration)
        )
    }
}
