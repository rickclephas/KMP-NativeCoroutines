package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.flow.flow
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeFlowAppleTests {

    @Test
    fun `ensure frozen`() {
        val flow = flow<RandomValue> { }
        assertFalse(flow.isFrozen, "Flow shouldn't be frozen yet")
        val nativeFlow = flow.asNativeFlow()
        assertTrue(nativeFlow.isFrozen, "NativeFlow should be frozen")
        assertTrue(flow.isFrozen, "Flow should be frozen")
    }
}