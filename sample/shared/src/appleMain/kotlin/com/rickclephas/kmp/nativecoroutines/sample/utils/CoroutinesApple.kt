package com.rickclephas.kmp.nativecoroutines.sample.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.initMainThread

@OptIn(ExperimentalCoroutinesApi::class)
fun initCoroutinesFromMainThread() = initMainThread()