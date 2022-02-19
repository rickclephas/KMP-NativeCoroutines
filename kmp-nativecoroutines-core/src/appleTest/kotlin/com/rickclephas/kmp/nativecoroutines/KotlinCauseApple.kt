package com.rickclephas.kmp.nativecoroutines

internal actual val NativeError.kotlinCause: Throwable?
    get() = this.userInfo["KotlinException"] as? Throwable
