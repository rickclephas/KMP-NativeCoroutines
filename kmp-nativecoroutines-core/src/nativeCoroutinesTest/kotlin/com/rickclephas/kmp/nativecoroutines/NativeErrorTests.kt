package com.rickclephas.kmp.nativecoroutines

import kotlin.native.concurrent.isFrozen
import kotlin.test.*

/**
 * Get the [Throwable] that is represented by the given [NativeError]
 */
internal expect val NativeError.kotlinCause: Throwable?

class NativeErrorTests {
    @Test
    fun `ensure frozen`() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNativeError()
        assertTrue(nsError.isFrozen, "NSError should be frozen")
        assertTrue(exception.isFrozen, "Exception should be frozen")
    }

    @Test
    fun `ensure exception is part of user info`() {
        val exception = RandomException()
        val nsError = exception.asNativeError()
        assertSame(exception, nsError.kotlinCause, "Exception isn't part of the NativeError")
    }
}
