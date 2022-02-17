package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey
import kotlin.native.concurrent.freeze

actual typealias NativeError = NSError

internal actual fun Throwable.asNativeError(): NativeError = this.asNSError()

internal actual val NativeError.kotlinCause
    get() = this.userInfo["KotlinException"] as? Throwable

/**
 * Converts a [Throwable] to a [NSError].
 *
 * The returned [NSError] has `KotlinException` as the [NSError.domain], `0` as the [NSError.code] and
 * the [NSError.localizedDescription] is set to the [Throwable.message].
 *
 * The Kotlin throwable can be retrieved from the [NSError.userInfo] with the key `KotlinException`.
 */
@OptIn(UnsafeNumber::class)
internal fun Throwable.asNSError(): NSError {
    val userInfo = mutableMapOf<Any?, Any>()
    userInfo["KotlinException"] = this.freeze()
    val message = message
    if (message != null) {
        userInfo[NSLocalizedDescriptionKey] = message
    }
    return NSError.errorWithDomain("KotlinException", 0.convert(), userInfo)
}