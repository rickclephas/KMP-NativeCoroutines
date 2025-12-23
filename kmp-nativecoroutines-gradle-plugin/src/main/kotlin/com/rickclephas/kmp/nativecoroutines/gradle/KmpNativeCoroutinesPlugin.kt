package com.rickclephas.kmp.nativecoroutines.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

@Suppress("unused")
public class KmpNativeCoroutinesPlugin: KotlinCompilerPluginSupportPlugin {
    private companion object {

        const val KOTLIN_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"

        val KotlinTarget.isKmpNativeCoroutinesTarget: Boolean
            get() = this is KotlinNativeTarget && konanTarget.family.isAppleFamily
    }

    override fun apply(target: Project) {
        target.extensions.create("nativeCoroutines", KmpNativeCoroutinesExtension::class.java)
        target.pluginManager.withPlugin(KOTLIN_PLUGIN_ID) {
            val kotlin = target.extensions.getByType(KotlinMultiplatformExtension::class.java)
            val commonMainSourceSet = kotlin.sourceSets.getByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
            target.configurations.getByName(commonMainSourceSet.implementationConfigurationName).dependencies.apply {
                add(target.dependencies.create("com.rickclephas.kmp:kmp-nativecoroutines-core:$VERSION"))
                add(target.dependencies.create("com.rickclephas.kmp:kmp-nativecoroutines-annotations:$VERSION"))
            }
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.target.isKmpNativeCoroutinesTarget

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(KmpNativeCoroutinesExtension::class.java)
        return project.provider {
            buildList {
                add(SubpluginOption("exposedSeverity", extension.exposedSeverity.name))
                extension.generatedSourceDirs.map { project.file(it).absolutePath }.distinct().forEach {
                    add(SubpluginOption("generatedSourceDir", it))
                }
                add(SubpluginOption("suffix", extension.suffix))
                extension.flowValueSuffix?.let { add(SubpluginOption("flowValueSuffix", it)) }
                extension.flowReplayCacheSuffix?.let { add(SubpluginOption("flowReplayCacheSuffix", it)) }
                add(SubpluginOption("stateSuffix", extension.stateSuffix))
                extension.stateFlowSuffix?.let { add(SubpluginOption("stateFlowSuffix", it)) }
                val swiftExport = extension.swiftExportVersion.takeIf { extension.swiftExport } ?: 0
                add(SubpluginOption("swiftExport", swiftExport.toString()))
            }
        }
    }

    override fun getCompilerPluginId(): String = "com.rickclephas.kmp.nativecoroutines"

    override fun getPluginArtifactForNative(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler", VERSION)

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler-embeddable", VERSION)
}
