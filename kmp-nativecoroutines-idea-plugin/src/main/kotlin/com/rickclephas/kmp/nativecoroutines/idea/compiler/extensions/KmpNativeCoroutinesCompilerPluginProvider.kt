package com.rickclephas.kmp.nativecoroutines.idea.compiler.extensions

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCompilerPluginRegistrar
import org.jetbrains.kotlin.idea.fir.extensions.CompilerPluginRegistrarUtils
import org.jetbrains.kotlin.idea.fir.extensions.KotlinBundledFirCompilerPluginProvider
import java.nio.file.Path

public class KmpNativeCoroutinesCompilerPluginProvider: KotlinBundledFirCompilerPluginProvider {

    private val registrarClass = KmpNativeCoroutinesCompilerPluginRegistrar::class

    override fun provideBundledPluginJar(project: Project, userSuppliedPluginJar: Path): Path? {
        val registrarContent = CompilerPluginRegistrarUtils.readRegistrarContent(userSuppliedPluginJar) ?: return null
        if (registrarContent.trim() != registrarClass.qualifiedName) return null
        return PathManager.getJarForClass(registrarClass.java)
    }
}
