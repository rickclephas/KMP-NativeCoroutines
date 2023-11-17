package com.rickclephas.kmp.nativecoroutines.compiler.utils

internal sealed class CoroutinesReturnType private constructor() {
    data object CoroutineScope: CoroutinesReturnType()
    sealed class Flow private constructor(): CoroutinesReturnType() {
        data object Generic: Flow()
        data object State: Flow()
    }
}
