package com.rickclephas.kmp.nativecoroutines

import kotlin.test.*

class NativeCallbackTests {

    @Test
    fun ensureInvoked() {
        var invokeCount = 0
        var receivedValue: RandomValue? = null
        val callback: NativeCallback<RandomValue> = callback@{ value, unit ->
            receivedValue = value
            invokeCount++
            // This isn't required in Kotlin, but it is in Swift, so we'll test it anyway
            return@callback unit
        }
        val value = RandomValue()
        callback(value)
        assertEquals(1, invokeCount, "NativeCallback should have been invoked once")
        assertSame(value, receivedValue, "Received value should be the same as the send value")
    }

    @Test
    fun ensureInvoked2() {
        var invokeCount = 0
        var receivedValue1: RandomValue? = null
        var receivedValue2: RandomValue? = null
        val callback: NativeCallback2<RandomValue, RandomValue> = callback@{ value1, value2, unit ->
            receivedValue1 = value1
            receivedValue2 = value2
            invokeCount++
            // This isn't required in Kotlin, but it is in Swift, so we'll test it anyway
            return@callback unit
        }
        val value1 = RandomValue()
        val value2 = RandomValue()
        callback(value1, value2)
        assertEquals(1, invokeCount, "NativeCallback should have been invoked once")
        assertSame(value1, receivedValue1, "Received value 1 should be the same as send value 1")
        assertSame(value2, receivedValue2, "Received value 2 should be the same as send value 2")
    }
}