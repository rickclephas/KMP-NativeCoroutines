package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
object Clock {

    @Suppress("ObjectPropertyName")
    private val _time = MutableStateFlow(0L)
    @NativeCoroutines
    val time = _time.asStateFlow()

    init {
        GlobalScope.launch {
            while (true) {
                _time.value = epochSeconds()
                delay(1.seconds)
            }
        }
    }
}

expect fun epochSeconds(): Long
