package com.rickclephas.kmp.nativecoroutines

actual typealias NativeError = Throwable

internal actual fun Throwable.asNativeError(): NativeError = this