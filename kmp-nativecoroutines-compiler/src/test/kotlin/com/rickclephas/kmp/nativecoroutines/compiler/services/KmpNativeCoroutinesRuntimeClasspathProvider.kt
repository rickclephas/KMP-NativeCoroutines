package com.rickclephas.kmp.nativecoroutines.compiler.services

import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TestServices
import java.io.File

internal sealed class KmpNativeCoroutinesRuntimeClasspathProvider(
    testServices: TestServices,
    private val propertyKey: String
): RuntimeClasspathProvider(testServices) {
    override fun runtimeClassPaths(module: TestModule): List<File> =
        System.getProperty(propertyKey).split(File.pathSeparator).map(::File)
}

internal class KmpNativeCoroutinesJvmRuntimeClasspathProvider(
    testServices: TestServices
): KmpNativeCoroutinesRuntimeClasspathProvider(
    testServices, "com.rickclephas.kmp.nativecoroutines.test.classpath-jvm"
)

internal class KmpNativeCoroutinesNativeRuntimeClasspathProvider(
    testServices: TestServices
): KmpNativeCoroutinesRuntimeClasspathProvider(
    testServices, "com.rickclephas.kmp.nativecoroutines.test.classpath-native"
)
