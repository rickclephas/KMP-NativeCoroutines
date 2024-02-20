package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FlowTests {

    @Test
    fun ensureFlowIsOriginal() {
        val flow = MutableSharedFlow<RandomValue>()
        val nativeFlow = flow.asNativeFlow(UnsupportedCoroutineScope)
        val actualFlow = nativeFlow.asFlow()
        assertSame(flow, actualFlow)
    }

    @Test
    fun ensureCompletionIsReceived() = runTest {
        val nativeFlow: NativeFlow<RandomValue> = nativeFlow@{
                returnType: String?,
                onItem: NativeCallback2<RandomValue, () -> NativeUnit>,
                onComplete: NativeCallback<NativeError?>,
                onCancelled: NativeCallback<NativeError> ->
            if (returnType != null) return@nativeFlow { null }
            onComplete(null)
            return@nativeFlow { null }
        }
        val flow = nativeFlow.asFlow()
        flow.collect()
    }

    @Test
    fun ensureErrorsAreReceivedAsExceptions() = runTest {
        val error = RandomNativeError()
        val nativeFlow: NativeFlow<RandomValue> = nativeFlow@{
                returnType: String?,
                onItem: NativeCallback2<RandomValue, () -> NativeUnit>,
                onComplete: NativeCallback<NativeError?>,
                onCancelled: NativeCallback<NativeError> ->
            if (returnType != null) return@nativeFlow { null }
            onComplete(error)
            return@nativeFlow { null }
        }
        val flow = nativeFlow.asFlow()
        val exception = assertFailsWith<NativeErrorException> {
            flow.collect()
        }
        assertSame(error, exception.error)
    }

    @Test
    fun ensureCancellationsAreReceived() = runTest {
        val error = RandomNativeError()
        val nativeFlow: NativeFlow<RandomValue> = nativeFlow@{
                returnType: String?,
                onItem: NativeCallback2<RandomValue, () -> NativeUnit>,
                onComplete: NativeCallback<NativeError?>,
                onCancelled: NativeCallback<NativeError> ->
            if (returnType != null) return@nativeFlow { null }
            onCancelled(error)
            return@nativeFlow { null }
        }
        val flow = nativeFlow.asFlow()
        val exception = assertFailsWith<NativeErrorCancellationException> {
            flow.collect()
        }
        assertSame(error, exception.error)
    }

    @Test
    fun ensureValuesAreReceived() = runTest {
        val values = listOf(RandomValue(), RandomValue(), RandomValue(), RandomValue())
        val nativeFlow: NativeFlow<RandomValue> = nativeFlow@{
                returnType: String?,
                onItem: NativeCallback2<RandomValue, () -> NativeUnit>,
                onComplete: NativeCallback<NativeError?>,
                onCancelled: NativeCallback<NativeError> ->
            if (returnType != null) return@nativeFlow { null }
            var sendValueCount = 0
            fun sendNextItem() {
                if (sendValueCount !in values.indices) return
                val value = values[sendValueCount++]
                onItem(value, ::sendNextItem)
            }
            sendNextItem()
            onComplete(null)
            return@nativeFlow { null }
        }
        val flow = nativeFlow.asFlow()
        val actualValues = flow.toList()
        assertContentEquals(values, actualValues)
    }

    @Test
    fun ensureCancellableIsInvoked() = runTest {
        var cancellationCount = 0
        val nativeFlow: NativeFlow<RandomValue> = nativeFlow@{
                returnType: String?,
                onItem: NativeCallback2<RandomValue, () -> NativeUnit>,
                onComplete: NativeCallback<NativeError?>,
                onCancelled: NativeCallback<NativeError> ->
            if (returnType != null) return@nativeFlow { null }
            return@nativeFlow {
                cancellationCount++
                null
            }
        }
        val flow = nativeFlow.asFlow()
        val job = flow.launchIn(this)
        advanceUntilIdle()
        job.cancelAndJoin()
        assertEquals(1, cancellationCount, "Cancellable should be called once")
    }
}
