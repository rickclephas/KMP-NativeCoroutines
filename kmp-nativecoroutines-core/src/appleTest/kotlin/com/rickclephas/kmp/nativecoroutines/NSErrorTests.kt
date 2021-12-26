package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.convert
import platform.Foundation.NSError
import kotlin.coroutines.cancellation.CancellationException
import kotlin.native.concurrent.isFrozen
import kotlin.native.internal.ObjCErrorException
import kotlin.random.Random
import kotlin.test.*

class NSErrorTests {

    @Test
    fun `ensure frozen`() {
        val exception = RandomException()
        assertFalse(exception.isFrozen, "Exception shouldn't be frozen yet")
        val nsError = exception.asNSError(arrayOf(RandomException::class))
        assertTrue(nsError.isFrozen, "NSError should be frozen")
        assertTrue(exception.isFrozen, "Exception should be frozen")
    }

    @Test
    fun `ensure NSError domain and code are correct`() {
        val exception = RandomException()
        val nsError = exception.asNSError(arrayOf(RandomException::class))
        assertEquals("KotlinException", nsError.domain, "Incorrect NSError domain")
        assertEquals(0.convert(), nsError.code, "Incorrect NSError code")
    }

    @Test
    fun `ensure localizedDescription is set to message`() {
        val exception = RandomException()
        val nsError = exception.asNSError(arrayOf(RandomException::class))
        assertEquals(exception.message, nsError.localizedDescription,
            "Localized description isn't set to message")
    }

    @Test
    fun `ensure exception is part of user info`() {
        val exception = RandomException()
        val nsError = exception.asNSError(arrayOf(RandomException::class))
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
    }

    @Test
    fun `ensure CancellationException is always propagated`() {
        val exception = CancellationException()
        val nsError = exception.asNSError(arrayOf())
        assertSame(exception, nsError.userInfo["KotlinException"], "Exception isn't part of the user info")
    }

    @Test
    fun `ensure ObjCErrorException is always propagated`() {
        val error = NSError.errorWithDomain(Random.nextString(), Random.nextInt().convert(), null)
        val exception = ObjCErrorException(Random.nextString(), error)
        val nsError = exception.asNSError(arrayOf())
        assertEquals(error, nsError, "NSError isn't equal")
    }
}