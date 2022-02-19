package com.rickclephas.kmp.nativecoroutines

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlin.native.concurrent.isFrozen
import kotlin.test.*

class NativeFlowTests {

    @Test
    fun `ensure not frozen`() {
        val flow = flow<RandomValue> {  }
        assertFalse(flow.isFrozen, "Flow shouldn't be frozen yet")
        val nativeFlow = flow.asNativeFlow()
        assertFalse(nativeFlow.isFrozen, "NativeFlow shouldn't be frozen")
        assertFalse(flow.isFrozen, "Flow shouldn't be frozen")
    }

    @Test
    fun `ensure completion callback is invoked`() = runBlocking {
        val flow = flow<RandomValue> {  }
        val job = Job()
        val nativeFlow = flow.asNativeFlow(CoroutineScope(job))
        val completionCount = atomic(0)
        nativeFlow({ _, _ -> }, { error, _ ->
            assertNull(error, "Flow should complete without an error")
            completionCount.incrementAndGet()
        })
        job.children.forEach { it.join() } // Waits for the collection to complete
        assertEquals(1, completionCount.value, "Completion callback should be called once")
    }

    @Test
    fun `ensure exceptions are received as errors`() = runBlocking {
        val exception = RandomException()
        val flow = flow<RandomValue> { throw exception }
        val job = Job()
        val nativeFlow = flow.asNativeFlow(CoroutineScope(job))
        val completionCount = atomic(0)
        nativeFlow({ _, _ -> }, { error, _ ->
            assertNotNull(error, "Flow should complete with an error")
            val kotlinException = error.userInfo["KotlinException"]
            assertSame(exception, kotlinException, "Kotlin exception should be the same exception")
            completionCount.incrementAndGet()
        })
        job.children.forEach { it.join() } // Waits for the collection to complete
        assertEquals(1, completionCount.value, "Completion callback should be called once")
    }

    @Test
    fun `ensure values are received`() = runBlocking {
        val values = listOf(RandomValue(), RandomValue(), RandomValue(), RandomValue())
        val flow = flow { values.forEach { emit(it) } }
        val job = Job()
        val nativeFlow = flow.asNativeFlow(CoroutineScope(job))
        val receivedValueCount = atomic(0)
        nativeFlow({ value, _ ->
            assertSame(values[receivedValueCount.value], value, "Received incorrect value")
            receivedValueCount.incrementAndGet()
        }, { _, _ -> })
        job.children.forEach { it.join() } // Waits for the collection to complete
        assertEquals(values.size, receivedValueCount.value, "Item callback should be called for every value")
    }

    @Test
    fun `ensure collection is cancelled`() = runBlocking {
        val flow = MutableSharedFlow<RandomValue>()
        val job = Job()
        val nativeFlow = flow.asNativeFlow(CoroutineScope(job))
        val completionCount = atomic(0)
        val cancel = nativeFlow({ _, _ -> }, { error, _ ->
            assertNotNull(error, "Flow should complete with an error")
            val exception = error.userInfo["KotlinException"]
            assertIs<CancellationException>(exception, "Error should contain CancellationException")
            completionCount.incrementAndGet()
        })
        delay(100) // Gives the collection some time to start
        cancel()
        job.children.forEach { it.join() } // Waits for the collection to complete
        assertEquals(1, completionCount.value, "Completion callback should be called once")
    }
}