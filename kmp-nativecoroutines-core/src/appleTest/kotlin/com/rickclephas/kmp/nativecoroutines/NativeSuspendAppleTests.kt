package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.delay
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeSuspendAppleTests {

    private suspend fun delayAndReturn(delay: Long, value: RandomValue): RandomValue {
        delay(delay)
        return value
    }

    @Test
    fun ensureNotFrozen() {
        val value = RandomValue()
        assertFalse(value.isFrozen, "Value shouldn't be frozen yet")
        val nativeSuspend = nativeSuspend { delayAndReturn(0, value) }
        assertFalse(nativeSuspend.isFrozen, "NativeSuspend shouldn't be frozen")
        assertFalse(value.isFrozen, "Value shouldn't be frozen")
    }
}