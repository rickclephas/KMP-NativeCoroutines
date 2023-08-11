package com.rickclephas.kmp.nativecoroutines.gradle

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

@Suppress("unused")
public class KmpNativeCoroutinesPlugin: KotlinCompilerPluginSupportPlugin {
    private companion object {

        const val kotlinPluginId = "org.jetbrains.kotlin.multiplatform"
        const val kspPluginId = "com.google.devtools.ksp"

        val KotlinTarget.isKmpNativeCoroutinesTarget: Boolean
            get() = this is KotlinNativeTarget && konanTarget.family.isAppleFamily
    }

    override fun apply(target: Project) {
        target.extensions.create("nativeCoroutines", KmpNativeCoroutinesExtension::class.java)
        val nativeCoroutines = target.extensions.getByType(KmpNativeCoroutinesExtension::class.java)
        target.pluginManager.withPlugin(kotlinPluginId) {
            val kotlin = target.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val commonMainSourceSet = kotlin.sourceSets.getByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
            target.configurations.getByName(commonMainSourceSet.implementationConfigurationName).dependencies.apply {
                add(target.dependencies.create("com.rickclephas.kmp:kmp-nativecoroutines-core:$VERSION"))
                add(target.dependencies.create("com.rickclephas.kmp:kmp-nativecoroutines-annotations:$VERSION"))
            }
            target.pluginManager.withPlugin(kspPluginId) {
                kotlin.targets.configureEach { kotlinTarget ->
                    if (!kotlinTarget.isKmpNativeCoroutinesTarget) return@configureEach
                    val kspConfigName = "ksp${kotlinTarget.targetName.replaceFirstChar { it.uppercaseChar() }}"
                    target.dependencies.add(kspConfigName, "com.rickclephas.kmp:kmp-nativecoroutines-ksp:$VERSION")
                }
                val ksp = target.extensions.getByType(KspExtension::class.java)
                ksp.arg(KspCommandLineArgumentProvider(nativeCoroutines))
            }
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.target.isKmpNativeCoroutinesTarget

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(KmpNativeCoroutinesExtension::class.java)
        return project.provider {
            listOf(SubpluginOption("exposedSeverity", extension.exposedSeverity.name))
        }
    }

    override fun getCompilerPluginId(): String = "com.rickclephas.kmp.nativecoroutines"

    override fun getPluginArtifactForNative(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler", VERSION)

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler-embeddable", VERSION)
}
