package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlinx.coroutines.CancellationException
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

public actual typealias NativeError = NSError

/**
 * Converts a [Throwable] to a [NSError].
 *
 * The returned [NSError] has `KotlinException` as the [NSError.domain], `0` as the [NSError.code] and
 * the [NSError.localizedDescription] is set to the [Throwable.message].
 *
 * The Kotlin throwable can be retrieved from the [NSError.userInfo] with the key `KotlinException`.
 *
 * @see asThrowable
 */
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual fun Throwable.asNativeError(): NativeError {
    if (this is NativeErrorException) return error
    if (this is NativeErrorCancellationException) return error
    val userInfo = mutableMapOf<Any?, Any>()
    userInfo["KotlinException"] = this
    val message = message
    if (message != null) {
        userInfo[NSLocalizedDescriptionKey] = message
    }
    return NSError.errorWithDomain("KotlinException", 0.convert(), userInfo)
}

/**
 * A [NSError] represented as an [Exception].
 * @property error The represented [NSError].
 * @see asThrowable
 */
@OptIn(UnsafeNumber::class)
public actual class NativeErrorException internal actual constructor(
    public actual val error: NSError
): Exception("Domain=${error.domain} Code=${error.code} ${error.localizedDescription}")

/**
 * Converts a [NSError] to a [Throwable].
 * @see asNativeError
 */
internal actual fun NativeError.asThrowable(): Throwable =
    userInfo["KotlinException"] as? Throwable ?: NativeErrorException(this)

/**
 * A [NSError] represented as a [CancellationException].
 * @property error The represented [NSError].
 * @see asCancellationException
 */
@OptIn(UnsafeNumber::class)
public actual class NativeErrorCancellationException internal actual constructor(
    public actual val error: NSError
): CancellationException("Domain=${error.domain} Code=${error.code} ${error.localizedDescription}")

/**
 * Converts a [NSError] to a [CancellationException].
 * @see asNativeError
 */
internal actual fun NativeError.asCancellationException(): CancellationException {
    val throwable = userInfo["KotlinException"] as? Throwable ?: return NativeErrorCancellationException(this)
    if (throwable is CancellationException) return throwable
    return CancellationException(throwable)
}
