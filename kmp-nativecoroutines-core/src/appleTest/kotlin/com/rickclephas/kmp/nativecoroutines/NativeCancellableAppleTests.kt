package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeCancellableAppleTests {

    @Test
    fun `ensure frozen`() {
        val job = Job()
        assertFalse(job.isFrozen, "Job shouldn't be frozen yet")
        val nativeCancellable = job.asNativeCancellable()
        assertTrue(nativeCancellable.isFrozen, "NativeCancellable should be frozen")
        assertTrue(job.isFrozen, "Job should be frozen after getting the NativeCancellable")
    }
}