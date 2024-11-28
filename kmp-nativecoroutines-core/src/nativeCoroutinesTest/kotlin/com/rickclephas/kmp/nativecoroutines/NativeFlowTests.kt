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
        nativeFlow(null, ::EmptyNativeCallback2, { error ->
            assertNull(error, "Flow should complete without an error")
            completionCount++
            null
        }, { _ ->
            cancellationCount++
            null
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
        nativeFlow(null, ::EmptyNativeCallback2, { error ->
            assertNotNull(error, "Flow should complete with an error")
            val kotlinException = error.kotlinCause
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            completionCount++
            null
        }, { _ ->
            cancellationCount++
            null
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
        nativeFlow(null, { value, next ->
            assertSame(values[receivedValueCount], value, "Received incorrect value")
            receivedValueCount++
            next()
        }, ::EmptyNativeCallback1, ::EmptyNativeCallback1)
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
        val cancel = nativeFlow(null, ::EmptyNativeCallback2, { _ ->
            completionCount++
            null
        }, { error ->
            val exception = error.kotlinCause
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            cancellationCount++
            null
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
        val cancellable = nativeFlow(RETURN_TYPE_KOTLIN_FLOW, ::EmptyNativeCallback2, ::EmptyNativeCallback1, ::EmptyNativeCallback1)
        assertSame(flow, cancellable())
    }

    @Test
    fun ensureUnknownReturnTypeReturnsNull() {
        val flow = MutableSharedFlow<RandomValue>()
        val nativeFlow = flow.asNativeFlow(UnsupportedCoroutineScope)
        val cancellable = nativeFlow("unknown", ::EmptyNativeCallback2, ::EmptyNativeCallback1, ::EmptyNativeCallback1)
        assertNull(cancellable())
    }
}
