package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

public class NewMemoryModelIntegrationTests: IntegrationTests() {

    public class MutableData {
        public var dataFromBackground: String? = null
        public var dataFromMain: String? = null
    }

    private fun randomString(): String {
        val chars = mutableListOf<Char>()
        repeat(5) {
            chars.add(Random.nextInt(65, 91).toChar())
        }
        return chars.joinToString("")
    }

//    @NativeCoroutines
    @Throws(Exception::class)
    public suspend fun generateRandomMutableData(): MutableData {
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
