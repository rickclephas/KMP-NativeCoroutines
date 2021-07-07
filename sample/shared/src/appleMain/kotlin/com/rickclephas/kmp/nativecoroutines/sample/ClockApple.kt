package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.asNativeFlow

// TODO: Remove extension properties completely
//val Clock.timeNative
//    get() = time.asNativeFlow()

val Clock.timeNativeValue
    get() = time.value