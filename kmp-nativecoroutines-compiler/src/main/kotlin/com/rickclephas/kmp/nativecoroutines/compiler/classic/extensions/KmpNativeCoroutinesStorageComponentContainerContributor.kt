package com.rickclephas.kmp.nativecoroutines.compiler.classic.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.GENERATED_SOURCE_DIR
import com.rickclephas.kmp.nativecoroutines.compiler.config.get
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesChecker
import com.rickclephas.kmp.nativecoroutines.compiler.config.K2_MODE
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform

internal class KmpNativeCoroutinesStorageComponentContainerContributor(
    private val configuration: CompilerConfiguration
): StorageComponentContainerContributor {

    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
    ) {
        val checker = KmpNativeCoroutinesChecker(
            exposedSeverity = configuration[EXPOSED_SEVERITY],
            generatedSourceDirs = configuration[GENERATED_SOURCE_DIR],
            isK2Mode = configuration[K2_MODE],
        )
        container.useInstance(checker)
    }
}
