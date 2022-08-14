package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class FlowIntegrationTests: IntegrationTests() {

    @NativeCoroutines
    fun getFlow(count: Int, delay: Long) = flow {
        repeat(count) {
            delay(delay)
            emit(it)
        }
    }

    @NativeCoroutines
    fun getFlowWithNull(count: Int, nullIndex: Int, delay: Long) = flow {
        repeat(count) {
            delay(delay)
            emit(if (it == nullIndex) null else it)
        }
    }

    @NativeCoroutines
    fun getFlowWithException(count: Int, exceptionIndex: Int, message: String, delay: Long) = flow {
        repeat(count) {
            delay(delay)
            if (it == exceptionIndex) throw Exception(message)
            emit(it)
        }
    }

    @NativeCoroutines
    fun getFlowWithError(count: Int, errorIndex: Int, message: String, delay: Long) = flow {
        repeat(count) {
            delay(delay)
            if (it == errorIndex) throw Error(message)
            emit(it)
        }
    }

    @NativeCoroutines
    fun getFlowWithCallback(count: Int, callbackIndex: Int, delay: Long, callback: () -> Unit) = flow {
        repeat(count) {
            delay(delay)
            if (it == callbackIndex) callback()
            emit(it)
        }
    }
}
