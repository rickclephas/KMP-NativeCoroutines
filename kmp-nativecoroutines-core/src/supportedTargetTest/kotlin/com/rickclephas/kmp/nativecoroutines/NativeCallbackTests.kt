package com.rickclephas.kmp.nativecoroutines

import kotlin.test.*

class NativeCallbackTests {
    @Test
    fun ensure_invoked() {
        var invokeCount = 0
        var receivedValue: RandomValue? = null
        val callback: NativeCallback<RandomValue> = callback@{ value, unit ->
            receivedValue = value
            invokeCount++
            // This isn't required in Kotlin but it is in Swift so we'll test it anyway
            return@callback unit
        }
        val value = RandomValue()
        callback(value)
        assertEquals(1, invokeCount, "NativeCallback should have been invoked once")
        assertSame(value, receivedValue, "Received value should be the same as the send value")
    }
}