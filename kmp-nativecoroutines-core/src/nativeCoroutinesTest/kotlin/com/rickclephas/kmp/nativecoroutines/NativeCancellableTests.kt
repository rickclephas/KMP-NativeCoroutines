package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NativeCancellableTests {

    @Test
    fun `ensure frozen`() {
        val job = Job()
        assertFalse(job.isFrozen, "Job shouldn't be frozen yet")
        val nativeCancellable = job.asNativeCancellable()
        assertTrue(nativeCancellable.isFrozen, "NativeCancellable should be frozen")
        assertTrue(job.isFrozen, "Job should be frozen after getting the NativeCancellable")
    }

    @Test
    fun `ensure that the job gets cancelled`() {
        val job = Job()
        val nativeCancellable = job.asNativeCancellable()
        assertFalse(job.isCancelled, "Job shouldn't be cancelled yet")
        nativeCancellable()
        assertTrue(job.isCancelled, "Job should be cancelled")
    }
}