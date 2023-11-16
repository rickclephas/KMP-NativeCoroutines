package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.classic.extensions.KmpNativeCoroutinesStorageComponentContainerContributor
import com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions.KmpNativeCoroutinesFirExtensionRegistrar
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
public class KmpNativeCoroutinesCompilerPluginRegistrar: CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        FirExtensionRegistrarAdapter.registerExtension(KmpNativeCoroutinesFirExtensionRegistrar(configuration))
        StorageComponentContainerContributor.registerExtension(
            KmpNativeCoroutinesStorageComponentContainerContributor(configuration)
        )
    }
}
