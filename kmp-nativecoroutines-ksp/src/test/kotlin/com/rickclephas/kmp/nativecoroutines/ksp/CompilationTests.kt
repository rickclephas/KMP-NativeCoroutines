package com.rickclephas.kmp.nativecoroutines.ksp

import com.tschuchort.compiletesting.*
import org.intellij.lang.annotations.Language
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.lang.Integer.max
import kotlin.test.assertEquals

open class CompilationTests {

    @Rule @JvmField val temporaryFolder: TemporaryFolder = TemporaryFolder()

    protected fun runKspTest(
        @Language("kotlin") inputContent: String,
        @Language("kotlin") outputContent: String,
        kspArgs: Map<String, String> = mapOf(
            "nativeCoroutines.suffix" to "Native",
            "nativeCoroutines.flowValueSuffix" to "Value",
            "nativeCoroutines.flowReplayCacheSuffix" to "ReplayCache",
            "nativeCoroutines.stateSuffix" to "Value",
            "nativeCoroutines.stateFlowSuffix" to "Flow"
        )
    ) {
        KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            sources = listOf(SourceFile.new("Source.kt", "package test\n\n$inputContent"))
            inheritClassPath = true
            symbolProcessorProviders = listOf(KmpNativeCoroutinesSymbolProcessorProvider())
            this.kspArgs += kspArgs
            assertCompile()
            assertKspSourceFile("test/SourceNative.kt", "package test\n\n$outputContent")
        }
    }
}

private fun KotlinCompilation.assertKspSourceFile(path: String, @Language("kotlin") contents: String) {
    val file = kspSourcesDir.resolve("kotlin/$path")
    assert(file.exists()) { "KSP source file <$path> doesn't exist." }
    val expectedLines = contents.lines()
    val actualLines = file.readLines()
    for (i in 0 until max(expectedLines.size, actualLines.size)) {
        assertEquals(expectedLines.getOrNull(i), actualLines.getOrNull(i),
            "File: <$path> doesn't have equal contents on line: <${i + 1}>")
    }
}

private fun KotlinCompilation.assertCompile(
    exitCode: KotlinCompilation.ExitCode = KotlinCompilation.ExitCode.OK
): KotlinCompilation.Result = compile().also {
    assertEquals(exitCode, it.exitCode)
}
