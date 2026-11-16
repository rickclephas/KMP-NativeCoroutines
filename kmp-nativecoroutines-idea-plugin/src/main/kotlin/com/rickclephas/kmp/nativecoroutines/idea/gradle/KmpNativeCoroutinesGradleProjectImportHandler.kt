package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.externalSystem.model.Key
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCommandLineProcessor
import org.jetbrains.kotlin.idea.compilerPlugin.CompilerPluginSetup.PluginOption
import org.jetbrains.kotlin.idea.gradleJava.compilerPlugin.AbstractCompilerPluginGradleImportHandler

public class KmpNativeCoroutinesGradleProjectImportHandler: AbstractCompilerPluginGradleImportHandler<KmpNativeCoroutinesModel>() {

    override val compilerPluginId: String = "com.rickclephas.kmp.nativecoroutines"
    override val modelKey: Key<KmpNativeCoroutinesModel> = KmpNativeCoroutinesModelKey
    override val pluginJarFileFromIdea: String
        get() = PathManager.getJarPathForClass(KmpNativeCoroutinesCommandLineProcessor::class.java)!!
    override val pluginName: String = "KMP-NativeCoroutines"

    override fun getOptions(
        model: KmpNativeCoroutinesModel
    ): List<PluginOption> = model.compilerPluginOptions().map { PluginOption(it.key, it.value) }
}
