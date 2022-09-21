package com.rickclephas.kmp.nativecoroutines.sample.tests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class NewMemoryModelIntegrationTests: IntegrationTests() {

    class MutableData {
        var dataFromBackground: String? = null
        var dataFromMain: String? = null
    }

    private fun randomString(): String {
        val chars = mutableListOf<Char>()
        repeat(5) {
            chars.add(Random.nextInt(65, 91).toChar())
        }
        return chars.joinToString("")
    }

    suspend fun generateRandomMutableData(): MutableData {
        val data = MutableData()
        withContext(Dispatchers.Main) {
            data.dataFromMain = randomString()
            withContext(Dispatchers.Default) {
                data.dataFromBackground = randomString()
            }
        }
        return data
    }
}