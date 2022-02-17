package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class AppleNativeSuspendTests {

    private suspend fun delayAndReturn(delay: Long, value: RandomValue): RandomValue {
        delay(delay)
        return value
    }

    private suspend fun delayAndThrow(delay: Long, exception: RandomException): RandomValue {
        delay(delay)
        throw exception
    }

    @Test
    fun `ensure frozen`() {
        val value = RandomValue()
        assertFalse(value.isFrozen, "Value shouldn't be frozen yet")
        val nativeSuspend = nativeSuspend { delayAndReturn(0, value) }
        assertTrue(nativeSuspend.isFrozen, "NativeSuspend should be frozen")
        assertTrue(value.isFrozen, "Value should be frozen")
    }

    @Test
    fun `ensure correct result is received`() = runBlocking {
        val value = RandomValue()
        val job = Job()
        val nativeSuspend = nativeSuspend(CoroutineScope(job)) { delayAndReturn(100, value) }
        val receivedResultCount = AtomicInt(0)
        val receivedErrorCount = AtomicInt(0)
        nativeSuspend({ receivedValue, _ ->
            assertSame(value, receivedValue, "Received incorrect value")
            receivedResultCount.increment()
        }, { _, _ ->
            receivedErrorCount.increment()
        })
        job.children.forEach { it.join() } // Waits for the function to complete
        assertEquals(1, receivedResultCount.value, "Result callback should be called once")
        assertEquals(0, receivedErrorCount.value, "Error callback shouldn't be called")
    }

    @Test
    fun `ensure exceptions are received as errors`() = runBlocking {
        val exception = RandomException()
        val job = Job()
        val nativeSuspend = nativeSuspend(CoroutineScope(job)) { delayAndThrow(100, exception) }
        val receivedResultCount = AtomicInt(0)
        val receivedErrorCount = AtomicInt(0)
        nativeSuspend({ _, _ ->
            receivedResultCount.increment()
        }, { error, _ ->
            assertNotNull(error, "Function should complete with an error")
            val kotlinException = error.userInfo["KotlinException"]
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            receivedErrorCount.increment()
        })
        job.children.forEach { it.join() } // Waits for the function to complete
        assertEquals(1, receivedErrorCount.value, "Error callback should be called once")
        assertEquals(0, receivedResultCount.value, "Result callback shouldn't be called")
    }

    @Test
    fun `ensure function is cancelled`() = runBlocking {
        val job = Job()
        val nativeSuspend = nativeSuspend(CoroutineScope(job)) { delayAndReturn(5_000, RandomValue()) }
        val receivedResultCount = AtomicInt(0)
        val receivedErrorCount = AtomicInt(0)
        val cancel = nativeSuspend({ _, _ ->
            receivedResultCount.increment()
        }, { error, _ ->
            assertNotNull(error, "Function should complete with an error")
            val exception = error.userInfo["KotlinException"]
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            receivedErrorCount.increment()
        })
        delay(100) // Gives the function some time to start
        cancel()
        job.children.forEach { it.join() } // Waits for the function to complete
        assertEquals(1, receivedErrorCount.value, "Error callback should be called once")
        assertEquals(0, receivedResultCount.value, "Result callback shouldn't be called")
    }
}