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
    fun `ensure frozen`() {
        val value = RandomValue()
        assertFalse(value.isFrozen, "Value shouldn't be frozen yet")
        val nativeSuspend = nativeSuspend { delayAndReturn(0, value) }
        assertTrue(nativeSuspend.isFrozen, "NativeSuspend should be frozen")
        assertTrue(value.isFrozen, "Value should be frozen")
    }
}