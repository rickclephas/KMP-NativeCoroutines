package com.rickclephas.kmp.nativecoroutines

/**
 * Represents an error in a way that the specific platform is able to handle
 */
expect class PlatformError

/**
 * Converts a [Throwable] to a [PlatformError].
 */
internal expect fun Throwable.asPlatformError(): PlatformError

internal expect val PlatformError.kotlinCause: Throwable?