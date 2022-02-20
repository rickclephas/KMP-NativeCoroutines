package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.sample.utils.freeze
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FlowIntegrationTests: IntegrationTests() {

    fun getFlow(count: Int, delay: Long): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            emit(it)
        }
    }

    fun getFlowWithNull(count: Int, nullIndex: Int, delay: Long): Flow<Int?> = flow {
        repeat(count) {
            delay(delay)
            emit(if (it == nullIndex) null else it)
        }
    }

    @Throws(Exception::class)
    fun getFlowWithException(count: Int, exceptionIndex: Int, message: String, delay: Long): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            if (it == exceptionIndex) throw Exception(message)
            emit(it)
        }
    }

    @Throws(Error::class)
    fun getFlowWithError(count: Int, errorIndex: Int, message: String, delay: Long): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            if (it == errorIndex) throw Error(message)
            emit(it)
        }
    }

    fun getFlowWithCallback(count: Int, callbackIndex: Int, delay: Long, callback: () -> Unit): Flow<Int> = flow {
        repeat(count) {
            delay(delay)
            if (it == callbackIndex) callback()
            emit(it)
        }
    }

    init {
        freeze()
    }
}