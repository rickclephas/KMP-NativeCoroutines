package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.application.PathManager
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCommandLineProcessor
import org.jetbrains.kotlin.idea.gradleJava.compilerPlugin.AbstractGradleImportHandler
import java.nio.file.Path

public class KmpNativeCoroutinesGradleProjectImportHandler: AbstractGradleImportHandler() {
    override val pluginJarsToReplaceRegex: List<Regex>
        get() = listOf("kmp-nativecoroutines-compiler-.*\\.jar".toRegex())
    override val replacementJarFromPluginBundle: Path
        get() = PathManager.getJarForClass(KmpNativeCoroutinesCommandLineProcessor::class.java)!!
}
