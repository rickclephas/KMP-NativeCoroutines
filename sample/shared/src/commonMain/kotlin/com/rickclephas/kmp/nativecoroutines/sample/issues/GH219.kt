package com.rickclephas.kmp.nativecoroutines.sample.issues

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

public interface GH219 {
    @NativeCoroutinesState
    public val state: StateFlow<String>
}

internal class GH219Impl: GH219 {
    override val state: StateFlow<String> = MutableStateFlow("GH219")
}

public fun createGH219(): GH219 = GH219Impl()
