package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
public object Clock {

    private val _time = MutableStateFlow(0L)
    @NativeCoroutines
    public val time: StateFlow<Long> = _time.asStateFlow()

    init {
        GlobalScope.launch {
            while (true) {
                _time.value = epochSeconds()
                delay(1.seconds)
            }
        }
    }
}

internal expect fun epochSeconds(): Long
