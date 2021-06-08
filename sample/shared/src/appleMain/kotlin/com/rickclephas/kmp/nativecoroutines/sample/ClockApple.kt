package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.asNativeFlow

val Clock.timeNative
    get() = time.asNativeFlow()

val Clock.timeNativeValue
    get() = time.value