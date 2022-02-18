package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlin.test.*

internal actual val NativeError.kotlinCause
    get() = this.userInfo["KotlinException"] as? Throwable

class NSErrorTests {
    @Test
    @OptIn(UnsafeNumber::class)
    fun `ensure NSError domain and code are correct`() {
        val exception = RandomException()
        val nsError = exception.asNativeError()
        assertEquals("KotlinException", nsError.domain, "Incorrect NSError domain")
        assertEquals(0.convert(), nsError.code, "Incorrect NSError code")
    }

    @Test
    fun `ensure localizedDescription is set to message`() {
        val exception = RandomException()
        val nsError = exception.asNativeError()
        assertEquals(exception.message, nsError.localizedDescription,
            "Localized description isn't set to message")
    }
}