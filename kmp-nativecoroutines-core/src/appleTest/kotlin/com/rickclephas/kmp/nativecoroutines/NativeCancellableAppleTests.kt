package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeCancellableAppleTests {

    @Test
    fun ensureNotFrozen() {
        val job = Job()
        assertFalse(job.isFrozen, "Job shouldn't be frozen yet")
        val nativeCancellable = job.asNativeCancellable()
        assertFalse(nativeCancellable.isFrozen, "NativeCancellable shouldn't be frozen")
        assertFalse(job.isFrozen, "Job shouldn't be frozen after getting the NativeCancellable")
    }
}
