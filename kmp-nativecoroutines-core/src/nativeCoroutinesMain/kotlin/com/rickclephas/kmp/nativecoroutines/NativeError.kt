package com.rickclephas.kmp.nativecoroutines

import kotlin.reflect.KClass

/**
 * Represents an error in a way that the specific platform is able to handle
 */
expect class NativeError

/**
 * Converts a [Throwable] to a [NativeError].
 *
 * @param propagatedExceptions an array of [Throwable] types that should be propagated to ObjC/Swift.
 */
internal expect fun Throwable.asNativeError(
    propagatedExceptions: Array<KClass<out Throwable>>
): NativeError
