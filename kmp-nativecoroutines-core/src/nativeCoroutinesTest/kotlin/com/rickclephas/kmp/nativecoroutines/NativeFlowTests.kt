package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NativeFlowTests {

    @Test
    fun ensureCompletionCallbackIsInvoked() = runTest {
        val flow = flow<RandomValue> { }
        val nativeFlow = flow.asNativeFlow(this)
        var completionCount = 0
        var cancellationCount = 0
        nativeFlow(null, { _, _, _ -> }, { error, _ ->
            assertNull(error, "Flow should complete without an error")
            completionCount++
        }, { _, _ ->
            cancellationCount++
        })
        advanceUntilIdle()
        assertEquals(1, completionCount, "Completion callback should be called once")
        assertEquals(0, cancellationCount, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureExceptionsAreReceivedAsErrors() = runTest {
        val exception = RandomException()
        val flow = flow<RandomValue> { throw exception }
        val nativeFlow = flow.asNativeFlow(this)
        var completionCount = 0
        var cancellationCount = 0
        nativeFlow(null,  { _, _, _ -> }, { error, _ ->
            assertNotNull(error, "Flow should complete with an error")
            val kotlinException = error.kotlinCause
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            completionCount++
        }, { _, _ ->
            cancellationCount++
        })
        advanceUntilIdle()
        assertEquals(1, completionCount, "Completion callback should be called once")
        assertEquals(0, cancellationCount, "Cancellation callback shouldn't be called")
    }

    @Test
    fun ensureValuesAreReceived() = runTest {
        val values = listOf(RandomValue(), RandomValue(), RandomValue(), RandomValue())
        val flow = flow { values.forEach { emit(it) } }
        val nativeFlow = flow.asNativeFlow(this)
        var receivedValueCount = 0
        nativeFlow(null, { value, next, _ ->
            assertSame(values[receivedValueCount], value, "Received incorrect value")
            receivedValueCount++
            next()
        }, { _, _ -> }, { _, _ -> })
        advanceUntilIdle()
        assertEquals(
            values.size,
            receivedValueCount,
            "Item callback should be called for every value"
        )
    }

    @Test
    fun ensureCollectionIsCancelled() = runTest {
        val flow = MutableSharedFlow<RandomValue>()
        val nativeFlow = flow.asNativeFlow(this)
        var completionCount = 0
        var cancellationCount = 0
        val cancel = nativeFlow(null, { _, _, _ -> }, { _, _ ->
            completionCount++
        }, { error, _ ->
            assertNotNull(error, "Flow should complete with a cancellation error")
            val exception = error.kotlinCause
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            cancellationCount++
        })
        delay(100) // Gives the collection some time to start
        cancel()
        advanceUntilIdle()
        assertEquals(1, cancellationCount, "Cancellation callback should be called once")
        assertEquals(0, completionCount, "Completion callback shouldn't be called")
    }

    @Test
    fun ensureFlowReturnTypeReturnsFlow() {
        val flow = MutableSharedFlow<RandomValue>()
        val nativeFlow = flow.asNativeFlow(UnsupportedCoroutineScope)
        val cancellable = nativeFlow(RETURN_TYPE_KOTLIN_FLOW, ::EmptyNativeCallback2, ::EmptyNativeCallback, ::EmptyNativeCallback)
        assertSame(flow, cancellable())
    }

    @Test
    fun ensureUnknownReturnTypeReturnsNull() {
        val flow = MutableSharedFlow<RandomValue>()
        val nativeFlow = flow.asNativeFlow(UnsupportedCoroutineScope)
        val cancellable = nativeFlow("unknown", ::EmptyNativeCallback2, ::EmptyNativeCallback, ::EmptyNativeCallback)
        assertNull(cancellable())
    }
}
