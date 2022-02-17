package com.rickclephas.kmp.nativecoroutines

actual typealias PlatformError = Throwable
actual fun Throwable.asPlatformError() = this
actual val PlatformError.kotlinCause: Throwable?
    get() = this