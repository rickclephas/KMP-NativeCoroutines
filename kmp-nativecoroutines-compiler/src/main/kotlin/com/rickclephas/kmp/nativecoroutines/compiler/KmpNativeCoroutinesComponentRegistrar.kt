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
        KmpNativeCoroutinesSyntheticResolveExtension(nameGenerator)
            .let { SyntheticResolveExtension.registerExtension(project, it) }
        KmpNativeCoroutinesSyntheticResolveExtension.RecursiveCallSyntheticResolveExtension()
            .let { SyntheticResolveExtension.registerExtension(project, it) }
        KmpNativeCoroutinesIrGenerationExtension(
            nameGenerator,
            configuration.getList(PROPAGATED_EXCEPTIONS_KEY),
            configuration.get(USE_THROWS_ANNOTATION_KEY, true)
        ).let { IrGenerationExtension.registerExtension(project, it) }
    }
}