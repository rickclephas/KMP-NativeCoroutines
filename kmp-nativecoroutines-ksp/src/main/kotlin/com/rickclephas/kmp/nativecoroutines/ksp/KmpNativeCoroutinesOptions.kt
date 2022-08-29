package com.rickclephas.kmp.nativecoroutines.ksp

internal class KmpNativeCoroutinesOptions(
    options: Map<String, String>
) {
    val suffix = options["nativeCoroutines.suffix"] ?: error("Missing required option: suffix")
    val fileSuffix = options["nativeCoroutines.fileSuffix"] ?: suffix
}
