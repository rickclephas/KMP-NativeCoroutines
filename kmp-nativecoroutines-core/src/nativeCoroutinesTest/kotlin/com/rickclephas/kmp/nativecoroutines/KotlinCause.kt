package com.rickclephas.kmp.nativecoroutines

/**
 * Gets the [Throwable] from the [NativeError].
 *
 * Note: this doesn't actually convert a [NativeError] to a [Throwable],
 * it should only be used to test [asNativeError] logic.
 */
internal expect val NativeError.kotlinCause: Throwable?