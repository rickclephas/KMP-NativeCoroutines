package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NativeCancellableTests {
    @Test
    fun ensure_that_the_job_gets_cancelled() {
        val job = Job()
        val nativeCancellable = job.asNativeCancellable()
        assertFalse(job.isCancelled, "Job shouldn't be cancelled yet")
        nativeCancellable()
        assertTrue(job.isCancelled, "Job should be cancelled")
    }
}