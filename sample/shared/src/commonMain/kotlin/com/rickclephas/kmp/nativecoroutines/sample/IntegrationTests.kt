package com.rickclephas.kmp.nativecoroutines.sample

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

class IntegrationTests {

    private val job = SupervisorJob()
    internal val coroutineScope = CoroutineScope(job + Dispatchers.Default)

    val activeJobCount: Int
        get() = job.children.count { it.isActive }

    val uncompletedJobCount: Int
        get() = job.children.count { !it.isCompleted }

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