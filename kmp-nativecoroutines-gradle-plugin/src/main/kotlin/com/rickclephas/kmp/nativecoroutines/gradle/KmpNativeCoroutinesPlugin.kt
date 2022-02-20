package com.rickclephas.kmp.nativecoroutines.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

@Suppress("unused")
class KmpNativeCoroutinesPlugin: KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("nativeCoroutines", KmpNativeCoroutinesExtension::class.java)
        target.afterEvaluate {
            val sourceSet = target.extensions.getByType(KotlinMultiplatformExtension::class.java)
                .sourceSets.getByName("commonMain")
            target.configurations.getByName(sourceSet.implementationConfigurationName).dependencies.apply {
                add(target.dependencies.create("com.rickclephas.kmp:kmp-nativecoroutines-core:$VERSION"))
                add(target.dependencies.create("com.rickclephas.kmp:kmp-nativecoroutines-annotations:$VERSION"))
            }
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.target.let { it is KotlinNativeTarget && it.konanTarget.family.isAppleFamily }

    @OptIn(ExperimentalStdlibApi::class)
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.findByType(KmpNativeCoroutinesExtension::class.java)
            ?: KmpNativeCoroutinesExtension()
        return project.provider {
            buildList {
                add(SubpluginOption("suffix", extension.suffix))
                extension.propagatedExceptions.forEach {
                    add(SubpluginOption("propagatedExceptions", it))
                }
                add(SubpluginOption("useThrowsAnnotation", extension.useThrowsAnnotation.toString()))
            }
        }
    }

    override fun getCompilerPluginId(): String = "com.rickclephas.kmp.nativecoroutines"

    override fun getPluginArtifactForNative(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler", VERSION)

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler-embeddable", VERSION)
}