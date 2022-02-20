package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.Foundation.NSError
import kotlin.coroutines.cancellation.CancellationException
import kotlin.native.concurrent.isFrozen
import kotlin.native.internal.ObjCErrorException
import kotlin.random.Random
import kotlin.test.*

class NativeErrorAppleTests {

    @Test
    fun ensureFrozen() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNativeError(arrayOf(RandomException::class))
        assertTrue(nsError.isFrozen, "NSError should be frozen")
        assertTrue(exception.isFrozen, "Exception should be frozen")
    }

    @Test
    @OptIn(UnsafeNumber::class)
    fun ensureNSErrorDomainAndCodeAreCorrect() {
        val exception = RandomException()
        val nsError = exception.asNativeError(arrayOf(RandomException::class))
        assertEquals("KotlinException", nsError.domain, "Incorrect NSError domain")
        assertEquals(0.convert(), nsError.code, "Incorrect NSError code")
    }

    @Test
    fun ensureLocalizedDescriptionIsSetToMessage() {
        val exception = RandomException()
        val nsError = exception.asNativeError(arrayOf(RandomException::class))
        assertEquals(exception.message, nsError.localizedDescription,
            "Localized description isn't set to message")
    }

    @Test
    fun ensureExceptionIsPartOfUserInfo() {
        val exception = RandomException()
        val nsError = exception.asNativeError(arrayOf(RandomException::class))
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
        assertSame(exception, nsError.kotlinCause, "Incorrect kotlinCause")
    }

    @Test
    fun `ensure CancellationException is always propagated`() {
        val exception = CancellationException()
        val nsError = exception.asNativeError(arrayOf())
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
    }

    @Test
    @OptIn(UnsafeNumber::class)
    fun `ensure ObjCErrorException is always propagated`() {
        val error = NSError.errorWithDomain(Random.nextString(), Random.nextInt().convert(), null)
        val exception = ObjCErrorException(Random.nextString(), error)
        val nsError = exception.asNativeError(arrayOf())
        assertEquals(error, nsError, "NSError isn't equal")
    }
}
