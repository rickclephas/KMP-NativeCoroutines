package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class SuspendTests {

    @Test
    fun ensureSuspendIsOriginal() = runTest {
        val nativeSuspend = nativeSuspend(UnsupportedCoroutineScope) { RandomValue() }
        nativeSuspend.await()
    }

    @Test
    fun ensureCorrectResultIsReceived() = runTest {
        val value = RandomValue()
        val nativeSuspend: NativeSuspend<RandomValue> = nativeSuspend@{
                returnType: String?,
                onResult: NativeCallback1<RandomValue>,
                onError: NativeCallback1<NativeError>,
                onCancelled: NativeCallback1<NativeError> ->
            if (returnType != null) return@nativeSuspend { null }
            onResult(value)
            return@nativeSuspend { null }
        }
        val actualValue = nativeSuspend.await()
        assertSame(value, actualValue)
    }

    @Test
    fun ensureErrorsAreReceivedAsExceptions() = runTest {
        val error = RandomNativeError()
        val nativeSuspend: NativeSuspend<RandomValue> = nativeSuspend@{
                returnType: String?,
                onResult: NativeCallback1<RandomValue>,
                onError: NativeCallback1<NativeError>,
                onCancelled: NativeCallback1<NativeError> ->
            if (returnType != null) return@nativeSuspend { null }
            onError(error)
            return@nativeSuspend { null }
        }
        val exception = assertFailsWith<NativeErrorException> {
            nativeSuspend.await()
        }
        assertSame(error, exception.error)
    }

    @Test
    fun ensureCancellationsAreReceived() = runTest {
        val error = RandomNativeError()
        val nativeSuspend: NativeSuspend<RandomValue> = nativeSuspend@{
                returnType: String?,
                onResult: NativeCallback1<RandomValue>,
                onError: NativeCallback1<NativeError>,
                onCancelled: NativeCallback1<NativeError> ->
            if (returnType != null) return@nativeSuspend { null }
            onCancelled(error)
            return@nativeSuspend { null }
        }
        val exception = assertFailsWith<NativeErrorCancellationException> {
            nativeSuspend.await()
        }
        assertSame(error, exception.error)
    }

    @Test
    fun ensureCancellableIsInvoked() = runTest {
        var cancellationCount = 0
        val nativeSuspend: NativeSuspend<RandomValue> = nativeSuspend@{
                returnType: String?,
                onResult: NativeCallback1<RandomValue>,
                onError: NativeCallback1<NativeError>,
                onCancelled: NativeCallback1<NativeError> ->
            if (returnType != null) return@nativeSuspend { null }
            return@nativeSuspend  {
                cancellationCount++
                null
            }
        }
        val job = launch { nativeSuspend.await() }
        advanceUntilIdle()
        job.cancelAndJoin()
        assertEquals(1, cancellationCount, "Cancellable should be called once")
    }
}
