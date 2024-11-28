package com.rickclephas.kmp.nativecoroutines

import kotlin.test.*

class NativeErrorTests {

    @Test
    fun ensureThrowableIsOriginal() {
        val exception = RandomException()
        val nativeError = exception.asNativeError()
        assertSame(exception, nativeError.asThrowable())
    }

    @Test
    fun ensureCancellationExceptionIsOriginal() {
        val cancellationException = RandomCancellationException()
        val nativeError = cancellationException.asNativeError()
        assertSame(cancellationException, nativeError.asCancellationException())
    }

    @Test
    fun ensureCancellationExceptionHasCause() {
        val exception = RandomException()
        val nativeError = exception.asNativeError()
        val cancellationException = nativeError.asCancellationException()
        val cause = cancellationException.cause
        assertNotNull(cause)
        assertSame(exception, cause)
    }

    @Test
    fun ensureNativeErrorExceptionHasNativeError() {
        val nativeError = RandomNativeError()
        val throwable = nativeError.asThrowable()
        assertIs<NativeErrorException>(throwable)
        assertSame(nativeError, throwable.error)
    }

    @Test
    fun ensureNativeErrorCancellationExceptionHasNativeError() {
        val nativeError = RandomNativeError()
        val cancellationException = nativeError.asCancellationException()
        assertIs<NativeErrorCancellationException>(cancellationException)
        assertSame(nativeError, cancellationException.error)
    }

    @Test
    fun ensureNativeErrorIsOriginal() {
        val nativeError = RandomNativeError()
        val throwable = nativeError.asThrowable()
        assertSame(nativeError, throwable.asNativeError())
    }

    @Test
    fun ensureNativeCancellationErrorIsOriginal() {
        val nativeError = RandomNativeError()
        val cancellationException = nativeError.asCancellationException()
        assertSame(nativeError, cancellationException.asNativeError())
    }
}
