package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public class SuspendIntegrationTests: IntegrationTests() {

    @NativeCoroutines
    public suspend fun returnValue(value: Int, delay: Long): Int {
        delay(delay)
        return value
    }

    @NativeCoroutines
    public suspend fun returnNull(delay: Long): Int? {
        delay(delay)
        return null
    }

    @NativeCoroutines
    public suspend fun throwException(message: String, delay: Long): Int {
        delay(delay)
        throw Exception(message)
    }

    @NativeCoroutines
    public suspend fun throwError(message: String, delay: Long): Int {
        delay(delay)
        throw Error(message)
    }

    @NativeCoroutines
    public suspend fun returnFromCallback(delay: Long, callback: () -> Int): Int {
        delay(delay)
        return callback()
    }

    @NativeCoroutines
    public suspend fun getFlow(count: Int, delay: Long): Flow<Int> {
        delay(delay)
        return flow {
            repeat(count) {
                delay(delay)
                emit(it)
            }
        }
    }
}
