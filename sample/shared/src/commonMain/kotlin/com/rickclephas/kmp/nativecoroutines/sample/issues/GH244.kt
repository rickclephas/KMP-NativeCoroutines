package com.rickclephas.kmp.nativecoroutines.sample.issues

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

public class GH244(public val value: String)

@NativeCoroutines
public suspend fun GH244.returnValueAfterDelay(seconds: Int): String {
    delay(seconds.seconds)
    return value
}
