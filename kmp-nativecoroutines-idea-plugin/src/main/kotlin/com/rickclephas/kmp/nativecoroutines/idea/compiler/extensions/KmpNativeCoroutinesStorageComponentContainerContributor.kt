package com.rickclephas.kmp.nativecoroutines.idea.compiler.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.ConfigOption
import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesChecker
import org.jetbrains.kotlin.analyzer.moduleInfo
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.idea.caches.project.ModuleSourceInfo
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformUnspecifiedTarget
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget

public class KmpNativeCoroutinesStorageComponentContainerContributor: StorageComponentContainerContributor {

    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
    ) {
        if (!platform.hasApple()) return

        val moduleInfo = moduleDescriptor.moduleInfo as? ModuleSourceInfo ?: return
        val kotlinFacet = KotlinFacet.get(moduleInfo.module) ?: return
        val pluginOptions = kotlinFacet.configuration.settings.compilerArguments?.pluginOptions ?: emptyArray()
        val exposedSeverity = pluginOptions.getPluginOption(EXPOSED_SEVERITY) ?: return

        container.useInstance(KmpNativeCoroutinesChecker(exposedSeverity))
    }

    private fun TargetPlatform.hasApple(): Boolean = isNotEmpty() && any {
        when (it) {
            is NativePlatformWithTarget -> it.target.family.isAppleFamily
            is NativePlatformUnspecifiedTarget -> true
            else -> false
        }
    }

    private fun <T: Any> Array<String>.getPluginOption(option: ConfigOption<T>): T? {
        val pluginId = "com.rickclephas.kmp.nativecoroutines"
        val prefix = "plugin:$pluginId:${option.optionName}="
        val value = firstOrNull { it.startsWith(prefix) }?.substring(prefix.length)
        return value?.let(option::parse)
    }
}
