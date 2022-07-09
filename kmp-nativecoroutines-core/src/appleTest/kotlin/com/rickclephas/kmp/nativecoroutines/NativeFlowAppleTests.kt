package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.flow.flow
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeFlowAppleTests {

    @Test
    fun ensureNotFrozen() {
        val flow = flow<RandomValue> {  }
        assertFalse(flow.isFrozen, "Flow shouldn't be frozen yet")
        val nativeFlow = flow.asNativeFlow()
        assertFalse(nativeFlow.isFrozen, "NativeFlow shouldn't be frozen")
        assertFalse(flow.isFrozen, "Flow shouldn't be frozen")
    }
}