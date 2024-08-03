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
        val nativeSuspend = nativeSuspend<RandomValue>(this) { delayAndReturn(100, value) }
        var receivedResultCount = 0
        var receivedErrorCount = 0
        var receivedCancellationCount = 0
        nativeSuspend(null, { receivedValue ->
            assertSame(value, receivedValue, "Received incorrect value")
            receivedResultCount++
            null
        }, { _ ->
            receivedErrorCount++
            null
        }, { _ ->
            receivedCancellationCount++
            null
        })
        advanceUntilIdle()
        assertEquals(1, receivedResultCount, "Result callback should be called once")
        assertEquals(0, receivedErrorCount, "Error callback shouldn't be called")
        assertEquals(0, receivedCancellationCount, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureExceptionsAreReceivedAsErrors() = runTest {
        val exception = RandomException()
        val nativeSuspend = nativeSuspend<RandomValue>(this) { delayAndThrow(100, exception) }
        var receivedResultCount = 0
        var receivedErrorCount = 0
        var receivedCancellationCount = 0
        nativeSuspend(null, { _ ->
            receivedResultCount++
            null
        }, { error ->
            val kotlinException = error.kotlinCause
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            receivedErrorCount++
            null
        }, { _ ->
            receivedCancellationCount++
            null
        })
        advanceUntilIdle()
        assertEquals(1, receivedErrorCount, "Error callback should be called once")
        assertEquals(0, receivedResultCount, "Result callback shouldn't be called")
        assertEquals(0, receivedCancellationCount, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureFunctionIsCancelled() = runTest {
        val nativeSuspend = nativeSuspend<RandomValue>(this) { delayAndReturn(5_000, RandomValue()) }
        var receivedResultCount = 0
        var receivedErrorCount = 0
        var receivedCancellationCount = 0
        val cancel = nativeSuspend(null, { _ ->
            receivedResultCount++
            null
        }, { _ ->
            receivedErrorCount++
            null
        }, { error ->
            val exception = error.kotlinCause
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            receivedCancellationCount++
            null
        })
        delay(100) // Gives the function some time to start
        cancel()
        advanceUntilIdle()
        assertEquals(1, receivedCancellationCount, "Cancellation callback should be called once")
        assertEquals(0, receivedErrorCount, "Error callback shouldn't be called")
        assertEquals(0, receivedResultCount, "Result callback shouldn't be called")
    }

    @Test
    fun ensureSuspendReturnTypeReturnsBlock() = runTest {
        val value = RandomValue()
        val block: (suspend () -> RandomValue) = { value }
        val nativeSuspend = nativeSuspend(UnsupportedCoroutineScope, block)
        val cancellable = nativeSuspend(RETURN_TYPE_KOTLIN_SUSPEND, ::EmptyNativeCallback1, ::EmptyNativeCallback1, ::EmptyNativeCallback1)
        assertSame(block, cancellable())
    }

    @Test
    fun ensureUnknownReturnTypeReturnsNull() = runTest {
        val nativeSuspend = nativeSuspend<RandomValue>(UnsupportedCoroutineScope) { RandomValue() }
        val cancellable = nativeSuspend("unknown", ::EmptyNativeCallback1, ::EmptyNativeCallback1, ::EmptyNativeCallback1)
        assertNull(cancellable())
    }
}
