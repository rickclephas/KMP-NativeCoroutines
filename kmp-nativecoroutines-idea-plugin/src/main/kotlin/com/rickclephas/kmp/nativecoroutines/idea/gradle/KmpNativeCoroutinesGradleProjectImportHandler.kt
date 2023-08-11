package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.Key
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCommandLineProcessor
import com.rickclephas.kmp.nativecoroutines.compiler.config.ConfigOption
import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import org.jetbrains.kotlin.idea.compilerPlugin.CompilerPluginSetup.PluginOption
import org.jetbrains.kotlin.idea.gradleJava.compilerPlugin.AbstractCompilerPluginGradleImportHandler

public class KmpNativeCoroutinesGradleProjectImportHandler: AbstractCompilerPluginGradleImportHandler<KmpNativeCoroutinesModel>() {

    override val compilerPluginId: String = "com.rickclephas.kmp.nativecoroutines"
    override val modelKey: Key<KmpNativeCoroutinesModel> = KmpNativeCoroutinesModelKey
    override val pluginJarFileFromIdea: String
        get() = PathManager.getJarPathForClass(KmpNativeCoroutinesCommandLineProcessor::class.java)!!
    override val pluginName: String = "KMP-NativeCoroutines"

    private infix fun <T: Any> ConfigOption<T>.to(value: String): PluginOption = PluginOption(optionName, value)

    override fun getOptions(
        model: KmpNativeCoroutinesModel
    ): List<PluginOption> = listOf(
        EXPOSED_SEVERITY to model.exposedSeverity
    )
}
