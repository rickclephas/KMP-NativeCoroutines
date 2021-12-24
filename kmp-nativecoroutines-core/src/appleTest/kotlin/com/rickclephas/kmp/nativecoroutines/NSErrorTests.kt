package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NSErrorTests {

    @Test
    fun `ensure frozen`() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNSError(listOf(RandomException::class))
        assertTrue(nsError.isFrozen, "NSError should be frozen")
        assertTrue(exception.isFrozen, "Exception should be frozen")
    }

    @Test
    @OptIn(UnsafeNumber::class)
    fun `ensure NSError domain and code are correct`() {
        val exception = RandomException()
        val nsError = exception.asNSError(listOf(RandomException::class))
        assertEquals("KotlinException", nsError.domain, "Incorrect NSError domain")
        assertEquals(0.convert(), nsError.code, "Incorrect NSError code")
    }

    @Test
    fun `ensure localizedDescription is set to message`() {
        val exception = RandomException()
        val nsError = exception.asNSError(listOf(RandomException::class))
        assertEquals(exception.message, nsError.localizedDescription,
            "Localized description isn't set to message")
    }

    @Test
    fun `ensure exception is part of user info`() {
        val exception = RandomException()
        val nsError = exception.asNSError(listOf(RandomException::class))
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
    }
}