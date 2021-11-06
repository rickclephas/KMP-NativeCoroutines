package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.convert
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NSErrorTests {

    @Test
    fun `ensure not frozen`() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNSError()
        // TODO: check why NSError is frozen
        // assertFalse(nsError.isFrozen, "NSError shouldn't be frozen")
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen")
    }

    @Test
    fun `ensure NSError domain and code are correct`() {
        val exception = RandomException()
        val nsError = exception.asNSError()
        assertEquals("KotlinException", nsError.domain, "Incorrect NSError domain")
        assertEquals(0.convert(), nsError.code, "Incorrect NSError code")
    }

    @Test
    fun `ensure localizedDescription is set to message`() {
        val exception = RandomException()
        val nsError = exception.asNSError()
        assertEquals(exception.message, nsError.localizedDescription,
            "Localized description isn't set to message")
    }

    @Test
    fun `ensure exception is part of user info`() {
        val exception = RandomException()
        val nsError = exception.asNSError()
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
    }
}