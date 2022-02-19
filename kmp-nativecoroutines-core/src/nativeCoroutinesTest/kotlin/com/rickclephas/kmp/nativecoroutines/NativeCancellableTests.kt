package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job
import kotlin.test.*

class NativeCancellableTests {

    @Test
    fun ensureThatTheJobGetsCancelled() {
        val job = Job()
        val nativeCancellable = job.asNativeCancellable()
        assertFalse(job.isCancelled, "Job shouldn't be cancelled yet")
        nativeCancellable()
        assertTrue(job.isCancelled, "Job should be cancelled")
    }
}