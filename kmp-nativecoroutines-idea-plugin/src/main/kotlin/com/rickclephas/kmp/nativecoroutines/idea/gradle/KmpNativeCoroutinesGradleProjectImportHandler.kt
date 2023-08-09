package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.application.PathManager
import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesOptionNames as OptionNames
import org.jetbrains.kotlin.idea.compilerPlugin.CompilerPluginSetup.PluginOption
import org.jetbrains.kotlin.idea.gradleJava.compilerPlugin.AbstractCompilerPluginGradleImportHandler

class KmpNativeCoroutinesGradleProjectImportHandler: AbstractCompilerPluginGradleImportHandler<KmpNativeCoroutinesModel>() {

    override val compilerPluginId = "com.rickclephas.kmp.nativecoroutines"
    override val modelKey = KmpNativeCoroutinesModelImpl.KEY
    override val pluginJarFileFromIdea
        get() = PathManager.getJarPathForClass(OptionNames::class.java)!!
    override val pluginName = "KMP-NativeCoroutines"

    override fun getOptions(
        model: KmpNativeCoroutinesModel
    ): List<PluginOption> = listOf(
        PluginOption(OptionNames.EXPOSED_SEVERITY, model.exposedSeverity)
    )
}
