package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.externalSystem.model.Key
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCommandLineProcessor
import com.rickclephas.kmp.nativecoroutines.compiler.config.ConfigListOption
import com.rickclephas.kmp.nativecoroutines.compiler.config.ConfigOption
import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.GENERATED_SOURCE_DIR
import org.jetbrains.kotlin.idea.compilerPlugin.CompilerPluginSetup.PluginOption
import org.jetbrains.kotlin.idea.gradleJava.compilerPlugin.AbstractCompilerPluginGradleImportHandler

public class KmpNativeCoroutinesGradleProjectImportHandler: AbstractCompilerPluginGradleImportHandler<KmpNativeCoroutinesModel>() {

    override val compilerPluginId: String = "com.rickclephas.kmp.nativecoroutines"
    override val modelKey: Key<KmpNativeCoroutinesModel> = KmpNativeCoroutinesModelKey
    override val pluginJarFileFromIdea: String
        get() = PathManager.getJarPathForClass(KmpNativeCoroutinesCommandLineProcessor::class.java)!!
    override val pluginName: String = "KMP-NativeCoroutines"

    private fun MutableList<PluginOption>.add(option: ConfigOption<*>, value: String) =
        add(PluginOption(option.optionName, value))

    private fun MutableList<PluginOption>.addAll(option: ConfigListOption<*>, values: List<String>) {
        for (value in values) {
            add(PluginOption(option.optionName, value))
        }
    }

    override fun getOptions(
        model: KmpNativeCoroutinesModel
    ): List<PluginOption> = buildList {
        add(EXPOSED_SEVERITY, model.exposedSeverity)
        addAll(GENERATED_SOURCE_DIR, model.generatedSourceDirs)
    }
}
