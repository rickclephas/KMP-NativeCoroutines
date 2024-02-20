package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlin.random.Random

internal actual val NativeError.kotlinCause: Throwable?
    get() = this.userInfo["KotlinException"] as? Throwable

@Suppress("TestFunctionName")
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual fun RandomNativeError(): NativeError =
    NativeError.errorWithDomain(Random.nextString(10), Random.nextInt(0, 50).convert(), null)
