package com.rickclephas.kmp.nativecoroutines

import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey
import kotlin.native.concurrent.freeze

/**
 * Converts an [Exception] to a [NSError].
 *
 * The returned [NSError] has `KotlinException` as the [NSError.domain], `0` as the [NSError.code] and
 * the [NSError.localizedDescription] is set to the [Exception.message].
 *
 * The Kotlin exception can be retrieved from the [NSError.userInfo] with the key `KotlinException`.
 */
internal fun Exception.asNSError(): NSError {
    val userInfo = mutableMapOf<Any?, Any>()
    userInfo["KotlinException"] = this.freeze()
    val message = message
    if (message != null) {
        userInfo[NSLocalizedDescriptionKey] = message
    }
    return NSError.errorWithDomain("KotlinException", 0, userInfo)
}