package com.rickclephas.kmp.nativecoroutines.compiler

import com.intellij.mock.MockProject
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NameGenerator
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

class KmpNativeCoroutinesComponentRegistrar: ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val suffix = configuration.get(SUFFIX_KEY) ?: return
        val nameGenerator =  NameGenerator(suffix)
        SyntheticResolveExtension.registerExtension(project, KmpNativeCoroutinesSyntheticResolveExtension(nameGenerator))
        SyntheticResolveExtension.registerExtension(project, KmpNativeCoroutinesSyntheticResolveExtension.RecursiveCallSyntheticResolveExtension())
        IrGenerationExtension.registerExtension(project, KmpNativeCoroutinesIrGenerationExtension(nameGenerator))
    }
}