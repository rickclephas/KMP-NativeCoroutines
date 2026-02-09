package com.rickclephas.kmp.nativecoroutines.idea.compiler.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesChecker
import com.rickclephas.kmp.nativecoroutines.compiler.config.*
import org.jetbrains.kotlin.analyzer.moduleInfo
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.idea.base.projectStructure.moduleInfo.ModuleSourceInfo
import org.jetbrains.kotlin.idea.base.util.K1ModeProjectStructureApi
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformUnspecifiedTarget
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget

@OptIn(K1ModeProjectStructureApi::class)
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
        val generatedSourceDirs = pluginOptions.getPluginOption(GENERATED_SOURCE_DIR)

        container.useInstance(KmpNativeCoroutinesChecker(exposedSeverity, generatedSourceDirs))
    }

    private fun TargetPlatform.hasApple(): Boolean = any {
        when (it) {
            is NativePlatformWithTarget -> it.target.family.isAppleFamily
            is NativePlatformUnspecifiedTarget -> true
            else -> false
        }
    }

    private val String.optionPrefix: String
        get() = "plugin:com.rickclephas.kmp.nativecoroutines:$this="

    private fun <T: Any> Array<String>.getPluginOption(option: ConfigOption<T>): T? {
        val prefix = option.optionName.optionPrefix
        val value = firstOrNull { it.startsWith(prefix) }?.substring(prefix.length)
        return value?.let(option::parse)
    }

    private fun <T: Any> Array<String>.getPluginOption(option: ConfigListOption<T>): List<T> {
        val prefix = option.optionName.optionPrefix
        return filter { it.startsWith(prefix) }.map { option.parse(it.substring(prefix.length)) }
    }
}
