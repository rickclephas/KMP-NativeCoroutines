package com.rickclephas.kmp.nativecoroutines.compiler.classic.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.get
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesChecker
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
        container.useInstance(KmpNativeCoroutinesChecker(configuration[EXPOSED_SEVERITY]))
    }
}
