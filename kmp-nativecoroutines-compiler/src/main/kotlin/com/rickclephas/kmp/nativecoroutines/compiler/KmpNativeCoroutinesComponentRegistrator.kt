package com.rickclephas.kmp.nativecoroutines.compiler

import com.google.auto.service.AutoService
import com.intellij.mock.MockProject
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

@AutoService(ComponentRegistrar::class)
class KmpNativeCoroutinesComponentRegistrator: ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val suffix = configuration.get(SUFFIX_KEY) ?: return
        SyntheticResolveExtension.registerExtension(project, KmpNativeCoroutinesSyntheticResolveExtension(suffix))
        IrGenerationExtension.registerExtension(project, KmpNativeCoroutinesIrGenerationExtension(suffix))
    }
}