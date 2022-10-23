package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
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
        var receivedResultCount = 0
        var receivedErrorCount = 0
        val receivedCancellationCount = 0
        nativeSuspend({ receivedValue, _ ->
            assertSame(value, receivedValue, "Received incorrect value")
            receivedResultCount++
        }, { _, _ ->
            receivedErrorCount++
        }, { _, _ ->
            receivedCancellationCount++
        })
        advanceUntilIdle()
        assertEquals(1, receivedResultCount, "Result callback should be called once")
        assertEquals(0, receivedErrorCount, "Error callback shouldn't be called")
        assertEquals(0, receivedCancellationCount, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureExceptionsAreReceivedAsErrors() = runTest {
        val exception = RandomException()
        val nativeSuspend = nativeSuspend(this) { delayAndThrow(100, exception) }
        var receivedResultCount = 0
        var receivedErrorCount = 0
        val receivedCancellationCount = 0
        nativeSuspend({ _, _ ->
            receivedResultCount++
        }, { error, _ ->
            assertNotNull(error, "Function should complete with an error")
            val kotlinException = error.kotlinCause
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            receivedErrorCount++
        }, { _, _ ->
            receivedCancellationCount++
        })
        advanceUntilIdle()
        assertEquals(1, receivedErrorCount, "Error callback should be called once")
        assertEquals(0, receivedResultCount, "Result callback shouldn't be called")
        assertEquals(0, receivedCancellationCount, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureFunctionIsCancelled() = runTest {
        val nativeSuspend = nativeSuspend(this) { delayAndReturn(5_000, RandomValue()) }
        var receivedResultCount = 0
        var receivedErrorCount = 0
        val receivedCancellationCount = 0
        val cancel = nativeSuspend({ _, _ ->
            receivedResultCount++
        }, { _, _ ->
            receivedErrorCount++
        }, { error, _ ->
            assertNotNull(error, "Function should complete with a cancellation error")
            val exception = error.kotlinCause
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            receivedCancellationCount++
        })
        delay(100) // Gives the function some time to start
        cancel()
        advanceUntilIdle()
        assertEquals(1, receivedCancellationCount, "Cancellation callback should be called once")
        assertEquals(0, receivedErrorCount, "Error callback shouldn't be called")
        assertEquals(0, receivedResultCount, "Result callback shouldn't be called")
    }
}
