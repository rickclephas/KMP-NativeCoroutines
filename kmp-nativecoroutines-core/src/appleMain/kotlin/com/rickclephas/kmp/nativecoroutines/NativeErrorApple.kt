package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

actual typealias NativeError = NSError

/**
 * Converts a [Throwable] to a [NSError].
 *
 * The returned [NSError] has `KotlinException` as the [NSError.domain], `0` as the [NSError.code] and
 * the [NSError.localizedDescription] is set to the [Throwable.message].
 *
 * The Kotlin throwable can be retrieved from the [NSError.userInfo] with the key `KotlinException`.
 */
@OptIn(UnsafeNumber::class)
internal actual fun Throwable.asNativeError(): NativeError {
    val userInfo = mutableMapOf<Any?, Any>()
    userInfo["KotlinException"] = this
    val message = message
    if (message != null) {
        userInfo[NSLocalizedDescriptionKey] = message
    }
    return NSError.errorWithDomain("KotlinException", 0.convert(), userInfo)
}
