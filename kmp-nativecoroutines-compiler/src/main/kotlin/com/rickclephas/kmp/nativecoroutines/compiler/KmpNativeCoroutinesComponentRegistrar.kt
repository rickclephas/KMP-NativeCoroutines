package com.rickclephas.kmp.nativecoroutines.compiler

import com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor

class KmpNativeCoroutinesComponentRegistrar: ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        StorageComponentContainerContributor.registerExtension(project, KmpNativeCoroutinesStorageComponentContainerContributor())
    }
}
