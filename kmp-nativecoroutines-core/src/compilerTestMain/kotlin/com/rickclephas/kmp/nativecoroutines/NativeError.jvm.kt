package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException

public actual typealias NativeError = Throwable

internal actual fun Throwable.asNativeError(): Throwable = this

public actual class NativeErrorException internal actual constructor(
    public actual val error: Throwable
): Exception(error.message)

internal actual fun NativeError.asThrowable(): Throwable = this

public actual class NativeErrorCancellationException internal actual constructor(
    public actual val error: Throwable
): CancellationException(error.message)

internal actual fun NativeError.asCancellationException(): CancellationException {
    if (this is CancellationException) return this
    return NativeErrorCancellationException(this)
}
