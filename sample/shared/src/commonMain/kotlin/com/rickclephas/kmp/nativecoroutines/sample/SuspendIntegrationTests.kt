package com.rickclephas.kmp.nativecoroutines.sample

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SuspendIntegrationTests: IntegrationTests() {

    suspend fun returnValue(value: Int, delay: Long): Int {
        delay(delay)
        return value
    }

    suspend fun returnNull(delay: Long): Int? {
        delay(delay)
        return null
    }

    suspend fun throwException(message: String, delay: Long): Int {
        delay(delay)
        throw Exception(message)
    }

    suspend fun throwError(message: String, delay: Long): Int {
        delay(delay)
        throw Error(message)
    }

    suspend fun returnFromCallback(delay: Long, callback: () -> Int): Int {
        delay(delay)
        return callback()
    }

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