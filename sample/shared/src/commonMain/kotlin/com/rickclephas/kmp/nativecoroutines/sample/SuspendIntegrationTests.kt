package com.rickclephas.kmp.nativecoroutines.sample

import kotlinx.coroutines.delay

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
}