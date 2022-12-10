package com.rickclephas.kmp.nativecoroutines.ksp

internal class KmpNativeCoroutinesOptions(
    options: Map<String, String>
) {
    val suffix = options["nativeCoroutines.suffix"] ?: error("Missing required option: suffix")
    val fileSuffix = options["nativeCoroutines.fileSuffix"] ?: suffix
    val flowValueSuffix = options["nativeCoroutines.flowValueSuffix"]
    val flowReplayCacheSuffix = options["nativeCoroutines.flowReplayCacheSuffix"]
    val stateSuffix = options["nativeCoroutines.stateSuffix"] ?: error("Missing required option: stateSuffix")
    val stateFlowSuffix = options["nativeCoroutines.stateFlowSuffix"]
}
