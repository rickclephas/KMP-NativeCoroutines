package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeErrorAppleTests {

    @Test
    fun `ensure mutable`() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNativeError()
        // Note: ObjC objects are always considered frozen even though they are still mutable
        // https://youtrack.jetbrains.com/issue/KT-50379
        assertTrue(nsError.isFrozen, "NSError should be frozen")
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen")
    }

    @Test
    @OptIn(UnsafeNumber::class)
    fun ensureNSErrorDomainAndCodeAreCorrect() {
        val exception = RandomException()
        val nsError = exception.asNativeError()
        assertEquals("KotlinException", nsError.domain, "Incorrect NSError domain")
        assertEquals(0.convert(), nsError.code, "Incorrect NSError code")
    }

    @Test
    fun ensureLocalizedDescriptionIsSetToMessage() {
        val exception = RandomException()
        val nsError = exception.asNativeError()
        assertEquals(exception.message, nsError.localizedDescription,
            "Localized description isn't set to message")
    }

    @Test
    fun ensureExceptionIsPartOfUserInfo() {
        val exception = RandomException()
        val nsError = exception.asNativeError()
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
        assertSame(exception, nsError.kotlinCause, "Incorrect kotlinCause")
    }
}
