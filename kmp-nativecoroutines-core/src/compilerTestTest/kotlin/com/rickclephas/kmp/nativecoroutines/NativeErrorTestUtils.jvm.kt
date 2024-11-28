package com.rickclephas.kmp.nativecoroutines

internal actual val NativeError.kotlinCause: Throwable? get() = this

@Suppress("TestFunctionName")
internal actual fun RandomNativeError(): NativeError = RandomException()
