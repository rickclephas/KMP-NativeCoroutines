package com.rickclephas.kmp.nativecoroutines.gradle

import org.gradle.process.CommandLineArgumentProvider

internal class KspCommandLineArgumentProvider(
    private val nativeCoroutines: KmpNativeCoroutinesExtension
): CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = listOfNotNull(
        "nativeCoroutines.suffix=${nativeCoroutines.suffix}",
        nativeCoroutines.fileSuffix?.let { "nativeCoroutines.fileSuffix=$it" },
        nativeCoroutines.flowValueSuffix?.let { "nativeCoroutines.flowValueSuffix=$it" },
        nativeCoroutines.flowReplayCacheSuffix?.let { "nativeCoroutines.flowReplayCacheSuffix=$it" },
        "nativeCoroutines.stateSuffix=${nativeCoroutines.stateSuffix}",
        nativeCoroutines.stateFlowSuffix?.let { "nativeCoroutines.stateFlowSuffix=$it" },
        "nativeCoroutines.k2Mode=${nativeCoroutines.k2Mode}",
    )
}
