package com.rickclephas.kmp.nativecoroutines.sample

import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

object RandomLettersGenerator {

    private class BestExceptionEver: RuntimeException("the best exception ever")

    @Throws(BestExceptionEver::class, CancellationException::class)
    suspend fun getRandomLetters(throwException: Boolean): String {
        delay(2.seconds)
        if (throwException) throw BestExceptionEver()
        val chars = mutableListOf<Char>()
        repeat(5) {
            chars.add(Random.nextInt(65, 91).toChar())
        }
        return chars.joinToString("")
    }
}