package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NameGenerator
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

@OptIn(ExperimentalCompilerApi::class)
class KmpNativeCoroutinesCompilerPluginRegistrar: CompilerPluginRegistrar() {

    override val supportsK2: Boolean = false // TODO: Add K2 support

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val suffix = configuration.get(SUFFIX_KEY) ?: return
        val nameGenerator =  NameGenerator(suffix)
        SyntheticResolveExtension.registerExtension(KmpNativeCoroutinesSyntheticResolveExtension(nameGenerator))
        SyntheticResolveExtension.registerExtension(KmpNativeCoroutinesSyntheticResolveExtension.RecursiveCallSyntheticResolveExtension())
        IrGenerationExtension.registerExtension(KmpNativeCoroutinesIrGenerationExtension(nameGenerator))
    }
}
