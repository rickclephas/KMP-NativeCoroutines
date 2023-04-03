package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SuspendIntegrationTests: IntegrationTests() {

    @NativeCoroutines
    suspend fun returnValue(value: Int, delay: Long): Int {
        delay(delay)
        return value
    }

    @NativeCoroutines
    suspend fun returnNull(delay: Long): Int? {
        delay(delay)
        return null
    }

    @NativeCoroutines
    suspend fun throwException(message: String, delay: Long): Int {
        delay(delay)
        throw Exception(message)
    }

    @NativeCoroutines
    suspend fun throwError(message: String, delay: Long): Int {
        delay(delay)
        throw Error(message)
    }

    @NativeCoroutines
    suspend fun returnFromCallback(delay: Long, callback: () -> Int): Int {
        delay(delay)
        return callback()
    }

    @NativeCoroutines
    suspend fun getFlow(count: Int, delay: Long): Flow<Int> {
        delay(delay)
        return flow {
            repeat(count) {
                delay(delay)
                emit(it)
            }
        }
    }
}
