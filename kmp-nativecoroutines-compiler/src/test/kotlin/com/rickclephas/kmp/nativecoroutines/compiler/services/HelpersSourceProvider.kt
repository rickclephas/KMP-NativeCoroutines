package com.rickclephas.kmp.nativecoroutines.compiler.services

import org.jetbrains.kotlin.test.directives.model.RegisteredDirectives
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.kotlin.test.services.TestServices
import java.io.File

internal class HelpersSourceProvider(
    testServices: TestServices,
    private val helperNames: Set<String>
): AdditionalSourceProvider(testServices) {

    companion object {
        const val kmpNativeCoroutinesAnnotations = "kmp-nativecoroutines-annotations"
        const val kotlinNative = "kotlin-native"
        const val kotlinxCoroutinesCore = "kotlinx-coroutines-core"
    }

    private val helpersDirPath = "./src/testData/helpers"

    override fun produceAdditionalFiles(globalDirectives: RegisteredDirectives, module: TestModule): List<TestFile> {
        val helpers = helperNames.map { helperName ->
            File(helpersDirPath, helperName).also {
                assert(it.isDirectory) { "Missing helper sources: $helperName" }
            }
        }
        return helpers.flatMap { helper ->
            helper.walkTopDown().mapNotNull { it.takeIf { it.isFile }?.toTestFile() }.toList()
        }
    }
}
