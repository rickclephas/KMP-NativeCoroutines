package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform

internal class KmpNativeCoroutinesStorageComponentContainerContributor(
    configuration: CompilerConfiguration
): StorageComponentContainerContributor {

    private val exposedSeverity = configuration.get(EXPOSED_SEVERITY_KEY, ExposedSeverity.WARNING)

    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
    ) {
        container.useInstance(KmpNativeCoroutinesChecker(exposedSeverity))
    }
}
