package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NSErrorTests {

    @Test
    fun `ensure mutable`() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNSError()
        // Note: ObjC objects are always considered frozen even though they are still mutable
        // https://youtrack.jetbrains.com/issue/KT-50379
        assertTrue(nsError.isFrozen, "NSError should be frozen")
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen")
    }

    @Test
    @OptIn(UnsafeNumber::class)
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