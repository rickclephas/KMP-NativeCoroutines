package com.rickclephas.kmp.nativecoroutines.sample

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, DelicateCoroutinesApi::class)
object Clock {

    @Suppress("ObjectPropertyName")
    private val _time = MutableStateFlow(0L)
    val time = _time.asStateFlow()

    init {
        GlobalScope.launch {
            while (true) {
                _time.value = epochSeconds()
                delay(seconds(1))
            }
        }
    }
}

expect fun epochSeconds(): Long