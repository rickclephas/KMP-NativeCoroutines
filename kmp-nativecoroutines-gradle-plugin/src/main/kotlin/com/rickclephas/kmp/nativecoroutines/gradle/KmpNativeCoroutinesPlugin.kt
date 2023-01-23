package com.rickclephas.kmp.nativecoroutines.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

@Suppress("unused")
class KmpNativeCoroutinesPlugin: KotlinCompilerPluginSupportPlugin {
    companion object {

        private const val kotlinPluginId = "org.jetbrains.kotlin.multiplatform"
        private const val kspPluginId = "com.google.devtools.ksp"

        private val KotlinTarget.isKmpNativeCoroutinesTarget: Boolean
            get() = this is KotlinNativeTarget && konanTarget.family.isAppleFamily

        private fun Project.setKSPArguments(block: ((String, String) -> Unit) -> Unit) {
            val ksp = extensions.getByName("ksp")
            val argMethod = Class.forName("com.google.devtools.ksp.gradle.KspExtension")
                .getDeclaredMethod("arg", String::class.java, String::class.java)
            block { key, value -> argMethod.invoke(ksp, key, value) }
        }
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
                kotlin.targets.filter { it.isKmpNativeCoroutinesTarget }.map { target ->
                    "ksp${target.targetName.replaceFirstChar { it.uppercaseChar() }}"
                }.forEach {
                    target.dependencies.add(it, "com.rickclephas.kmp:kmp-nativecoroutines-ksp:$VERSION")
                }
                target.setKSPArguments { arg ->
                    arg("nativeCoroutines.suffix", nativeCoroutines.suffix)
                    nativeCoroutines.fileSuffix?.let { arg("nativeCoroutines.fileSuffix", it) }
                    nativeCoroutines.flowValueSuffix?.let { arg("nativeCoroutines.flowValueSuffix", it) }
                    nativeCoroutines.flowReplayCacheSuffix?.let { arg("nativeCoroutines.flowReplayCacheSuffix", it) }
                    arg("nativeCoroutines.stateSuffix", nativeCoroutines.stateSuffix)
                    nativeCoroutines.stateFlowSuffix?.let { arg("nativeCoroutines.stateFlowSuffix", it) }
                }
            }
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.target.isKmpNativeCoroutinesTarget

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(KmpNativeCoroutinesExtension::class.java)
        return project.provider {
            listOf(SubpluginOption("suffix", extension.suffix))
        }
    }

    override fun getCompilerPluginId(): String = "com.rickclephas.kmp.nativecoroutines"

    override fun getPluginArtifactForNative(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler", VERSION)

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler-embeddable", VERSION)
}
