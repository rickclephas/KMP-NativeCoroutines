package com.rickclephas.kmp.nativecoroutines.idea

import com.intellij.openapi.project.Project
import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesChecker
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCommonCompilerArgumentsHolder
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatformUnspecifiedTarget
import org.jetbrains.kotlin.platform.konan.NativePlatformWithTarget
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class KmpNativeCoroutinesStorageComponentContainerContributor(
    private val project: Project
): StorageComponentContainerContributor {

    companion object {
        private fun TargetPlatform.hasApple(): Boolean = isNotEmpty() && any {
            // TODO: Is this really the best way to filter the platforms?
            it is NativePlatformUnspecifiedTarget ||
            it.safeAs<NativePlatformWithTarget>()?.target?.family?.isAppleFamily == true
        }
    }

    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
    ) {
        if (!platform.hasApple()) return
        // TODO: Verify that KMP-NativeCoroutines is used in this project
        // TODO: Get ExposedSeverity from build.gradle.kts
        val pluginOptions = KotlinCommonCompilerArgumentsHolder.getInstance(project).settings.pluginOptions

        container.useInstance(KmpNativeCoroutinesChecker(ExposedSeverity.ERROR))
    }
}
