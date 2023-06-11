package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public class FlowIntegrationTests: IntegrationTests() {

    private var _emittedCount = 0
    public val emittedCount: Int get() = _emittedCount

    @NativeCoroutines
    public fun getFlow(count: Int, delay: Long): Flow<Int> = flow {
        _emittedCount = 0
        repeat(count) {
            delay(delay)
            emit(it)
            _emittedCount++
        }
    }

    @NativeCoroutines
    public  fun getFlowWithNull(count: Int, nullIndex: Int, delay: Long): Flow<Int?> = flow {
        repeat(count) {
            delay(delay)
            emit(if (it == nullIndex) null else it)
        }
    }

    @NativeCoroutines
    public fun getFlowWithException(count: Int, exceptionIndex: Int, message: String, delay: Long): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            if (it == exceptionIndex) throw Exception(message)
            emit(it)
        }
    }

    @NativeCoroutines
    public fun getFlowWithError(count: Int, errorIndex: Int, message: String, delay: Long): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            if (it == errorIndex) throw Error(message)
            emit(it)
        }
    }

    @NativeCoroutines
    public fun getFlowWithCallback(count: Int, callbackIndex: Int, delay: Long, callback: () -> Unit): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            if (it == callbackIndex) callback()
            emit(it)
        }
    }
}
