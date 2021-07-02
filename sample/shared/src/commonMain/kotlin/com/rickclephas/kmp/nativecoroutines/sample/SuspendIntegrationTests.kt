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

    suspend fun returnFlow(value: Int, delay: Long): Flow<Int> {
        delay(delay)
        return flow {
            delay(delay)
            emit(value)
        }
    }

    suspend fun returnCustomFlow(value: Int, delay: Long): CustomFlow<Int> {
        delay(delay)
        return CustomFlow(flow {
            delay(delay)
            emit(value)
        })
    }

    class CustomFlow<T>(flow: Flow<T>): Flow<T> by flow
}