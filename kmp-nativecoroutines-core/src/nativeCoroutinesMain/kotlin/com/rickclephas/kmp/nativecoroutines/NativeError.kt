package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException

/**
 * Represents an error in a way that the specific platform is able to handle
 */
public expect class NativeError

/**
 * Converts a [Throwable] to a [NativeError].
 * @see asThrowable
 */
internal expect fun Throwable.asNativeError(): NativeError

/**
 * A [NativeError] represented as an [Exception].
 * @property error The represented [NativeError].
 * @see asThrowable
 */
public expect class NativeErrorException internal constructor(error: NativeError): Exception {
    public val error: NativeError
}

/**
 * Converts a [NativeError] to a [Throwable].
 * @see asNativeError
 */
internal expect fun NativeError.asThrowable(): Throwable

/**
 * A [NativeError] represented as a [CancellationException].
 * @property error The represented [NativeError].
 * @see asCancellationException
 */
public expect class NativeErrorCancellationException internal constructor(error: NativeError): CancellationException {
    public val error: NativeError
}

/**
 * Converts a [NativeError] to a [CancellationException].
 * @see asNativeError
 */
internal expect fun NativeError.asCancellationException(): CancellationException
