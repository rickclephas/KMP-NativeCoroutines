package com.rickclephas.kmp.nativecoroutines

public actual typealias NativeError = Throwable

internal actual fun Throwable.asNativeError(): Throwable = this
