package com.rickclephas.kmp.nativecoroutines.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*

@Suppress("unused")
class KmpNativeCoroutinesPlugin: KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("nativeCoroutines", KmpNativeCoroutinesExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.platformType == KotlinPlatformType.native

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.findByType(KmpNativeCoroutinesExtension::class.java)
            ?: KmpNativeCoroutinesExtension()
        return project.provider {
            listOf(SubpluginOption("suffix", extension.suffix))
        }
    }

    override fun getCompilerPluginId(): String = "com.rickclephas.kmp.nativecoroutines"

    override fun getPluginArtifactForNative(): SubpluginArtifact =
        SubpluginArtifact("com.rickclephas.kmp", "kmp-nativecoroutines-compiler", "0.2.0")

    override fun getPluginArtifact(): SubpluginArtifact =
        throw UnsupportedOperationException("KMP-NativeCoroutines only supports Kotlin\\Native!")
}