package com.rickclephas.kmp.nativecoroutines.idea.compiler.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesOptionNames
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesChecker
import org.jetbrains.kotlin.analyzer.moduleInfo
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.idea.base.projectStructure.moduleInfo.ModuleSourceInfo
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCommonCompilerArgumentsHolder
import org.jetbrains.kotlin.idea.facet.getInstance
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformUnspecifiedTarget
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget

class KmpNativeCoroutinesStorageComponentContainerContributor: StorageComponentContainerContributor {

    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
    ) {
        if (!platform.hasApple()) return

        val moduleInfo = moduleDescriptor.moduleInfo as? ModuleSourceInfo ?: return
        val pluginOptions = KotlinCommonCompilerArgumentsHolder.getInstance(moduleInfo.module).pluginOptions ?: emptyArray()
        val exposedSeverity = pluginOptions.getPluginOption(KmpNativeCoroutinesOptionNames.EXPOSED_SEVERITY) ?: return

        container.useInstance(KmpNativeCoroutinesChecker(ExposedSeverity.valueOf(exposedSeverity)))
    }

    private fun TargetPlatform.hasApple(): Boolean = isNotEmpty() && any {
        when (it) {
            is NativePlatformWithTarget -> it.target.family.isAppleFamily
            is NativePlatformUnspecifiedTarget -> true
            else -> false
        }
    }

    private fun Array<String>.getPluginOption(optionName: String): String? {
        val pluginId = "com.rickclephas.kmp.nativecoroutines"
        val prefix = "plugin:$pluginId:$optionName="
        return firstOrNull { it.startsWith(prefix) }?.substring(prefix.length)
    }
}
