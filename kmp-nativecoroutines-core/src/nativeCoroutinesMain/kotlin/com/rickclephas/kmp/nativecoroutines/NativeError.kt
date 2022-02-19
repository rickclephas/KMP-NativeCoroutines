package com.rickclephas.kmp.nativecoroutines

/**
 * Represents an error in a way that the specific platform is able to handle
 */
expect class NativeError

/**
 * Converts a [Throwable] to a [NativeError].
 */
internal expect fun Throwable.asNativeError(): NativeError
