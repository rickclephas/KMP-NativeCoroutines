package com.rickclephas.kmp.nativecoroutines

import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeCallbackAppleTests {

    @Test
    fun `ensure frozen`() {
        var receivedValue: RandomValue? = null
        val callback: NativeCallback<RandomValue> = callback@{ value, unit ->
            receivedValue = value
            // This isn't required in Kotlin, but it is in Swift, so we'll test it anyway
            return@callback unit
        }
        val value = RandomValue()
        assertFalse(value.isFrozen, "Value shouldn't be frozen yet")
        callback(value)
        assertTrue(value.isFrozen, "Value should be frozen")
        assertTrue(receivedValue?.isFrozen == true, "Received value should be frozen")
    }
}