package com.rickclephas.kmp.nativecoroutines

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlin.test.*

class NativeSuspendTests {

    private suspend fun delayAndReturn(delay: Long, value: RandomValue): RandomValue {
        delay(delay)
        return value
    }

    private suspend fun delayAndThrow(delay: Long, exception: RandomException): RandomValue {
        delay(delay)
        throw exception
    }

    @Test
    fun ensureCorrectResultIsReceived() = runBlocking {
        val value = RandomValue()
        val job = Job()
        val nativeSuspend = nativeSuspend(CoroutineScope(job)) { delayAndReturn(100, value) }
        val receivedResultCount = atomic(0)
        val receivedErrorCount = atomic(0)
        nativeSuspend({ receivedValue, _ ->
            assertSame(value, receivedValue, "Received incorrect value")
            receivedResultCount.incrementAndGet()
        }, { _, _ ->
            receivedErrorCount.incrementAndGet()
        })
        job.children.forEach { it.join() } // Waits for the function to complete
        assertEquals(1, receivedResultCount.value, "Result callback should be called once")
        assertEquals(0, receivedErrorCount.value, "Error callback shouldn't be called")
    }

    @Test
    fun ensureExceptionsAreReceivedAsErrors() = runBlocking {
        val exception = RandomException()
        val job = Job()
        val nativeSuspend = nativeSuspend(CoroutineScope(job)) { delayAndThrow(100, exception) }
        val receivedResultCount = atomic(0)
        val receivedErrorCount = atomic(0)
        nativeSuspend({ _, _ ->
            receivedResultCount.incrementAndGet()
        }, { error, _ ->
            assertNotNull(error, "Function should complete with an error")
            val kotlinException = error.kotlinCause
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            receivedErrorCount.incrementAndGet()
        })
        job.children.forEach { it.join() } // Waits for the function to complete
        assertEquals(1, receivedErrorCount.value, "Error callback should be called once")
        assertEquals(0, receivedResultCount.value, "Result callback shouldn't be called")
    }

    @Test
    fun ensureFunctionIsCancelled() = runBlocking {
        val job = Job()
        val nativeSuspend = nativeSuspend(CoroutineScope(job)) { delayAndReturn(5_000, RandomValue()) }
        val receivedResultCount = atomic(0)
        val receivedErrorCount = atomic(0)
        val cancel = nativeSuspend({ _, _ ->
            receivedResultCount.incrementAndGet()
        }, { error, _ ->
            assertNotNull(error, "Function should complete with an error")
            val exception = error.kotlinCause
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            receivedErrorCount.incrementAndGet()
        })
        delay(100) // Gives the function some time to start
        cancel()
        job.children.forEach { it.join() } // Waits for the function to complete
        assertEquals(1, receivedErrorCount.value, "Error callback should be called once")
        assertEquals(0, receivedResultCount.value, "Result callback shouldn't be called")
    }
}