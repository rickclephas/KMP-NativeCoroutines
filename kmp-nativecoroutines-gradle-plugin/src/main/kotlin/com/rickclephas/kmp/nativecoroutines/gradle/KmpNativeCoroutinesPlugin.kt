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
            val generatedSourceDirs = extension.generatedSourceDirs.map { project.file(it) }.distinct().map { dir ->
                // Prefer project relative paths so that the absolute project path doesn't
                // end up in the build cache key of Kotlin/Native compilations, which would
                // prevent relocated build cache hits.
                val relativeDir = dir.relativeToOrNull(project.projectDir)?.takeUnless {
                    it.path.startsWith("..")
                }
                (relativeDir ?: dir).path
            }
            extension.toCompilerPluginOptions(generatedSourceDirs)
        }
    }

    override fun getCompilerPluginId(): String = "com.rickclephas.kmp.nativecoroutines"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler-embeddable", VERSION)
}
