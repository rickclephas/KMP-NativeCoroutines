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
    fun ensureCorrectResultIsReceived() = runTest {
        val value = RandomValue()
        val nativeSuspend = nativeSuspend(this) { delayAndReturn(100, value) }
        val receivedResultCount = atomic(0)
        val receivedErrorCount = atomic(0)
        val receivedCancellationCount = atomic(0)
        nativeSuspend({ receivedValue, _ ->
            assertSame(value, receivedValue, "Received incorrect value")
            receivedResultCount.incrementAndGet()
        }, { _, _ ->
            receivedErrorCount.incrementAndGet()
        }, { _, _ ->
            receivedCancellationCount.incrementAndGet()
        })
        runCurrent()
        assertEquals(1, receivedResultCount.value, "Result callback should be called once")
        assertEquals(0, receivedErrorCount.value, "Error callback shouldn't be called")
        assertEquals(0, receivedCancellationCount.value, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureExceptionsAreReceivedAsErrors() = runTest {
        val exception = RandomException()
        val nativeSuspend = nativeSuspend(this) { delayAndThrow(100, exception) }
        val receivedResultCount = atomic(0)
        val receivedErrorCount = atomic(0)
        val receivedCancellationCount = atomic(0)
        nativeSuspend({ _, _ ->
            receivedResultCount.incrementAndGet()
        }, { error, _ ->
            assertNotNull(error, "Function should complete with an error")
            val kotlinException = error.kotlinCause
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            receivedErrorCount.incrementAndGet()
        }, { _, _ ->
            receivedCancellationCount.incrementAndGet()
        })
        runCurrent()
        assertEquals(1, receivedErrorCount.value, "Error callback should be called once")
        assertEquals(0, receivedResultCount.value, "Result callback shouldn't be called")
        assertEquals(0, receivedCancellationCount.value, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureFunctionIsCancelled() = runTest {
        val nativeSuspend = nativeSuspend(this) { delayAndReturn(5_000, RandomValue()) }
        val receivedResultCount = atomic(0)
        val receivedErrorCount = atomic(0)
        val receivedCancellationCount = atomic(0)
        val cancel = nativeSuspend({ _, _ ->
            receivedResultCount.incrementAndGet()
        }, { _, _ ->
            receivedErrorCount.incrementAndGet()
        }, { error, _ ->
            assertNotNull(error, "Function should complete with a cancellation error")
            val exception = error.kotlinCause
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            receivedCancellationCount.incrementAndGet()
        })
        delay(100) // Gives the function some time to start
        cancel()
        runCurrent()
        assertEquals(1, receivedCancellationCount.value, "Cancellation callback should be called once")
        assertEquals(0, receivedErrorCount.value, "Error callback shouldn't be called")
        assertEquals(0, receivedResultCount.value, "Result callback shouldn't be called")
    }
}
