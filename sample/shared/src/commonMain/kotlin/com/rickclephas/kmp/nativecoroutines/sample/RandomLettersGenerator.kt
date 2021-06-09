package com.rickclephas.kmp.nativecoroutines.sample

import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object RandomLettersGenerator {

    suspend fun getRandomLetters(throwException: Boolean): String {
        delay(seconds(2))
        if (throwException) {
            throw RuntimeException("the best exception ever")
        }
        val chars = mutableListOf<Char>()
        repeat(5) {
            chars.add(Random.nextInt(65, 91).toChar())
        }
        return chars.joinToString("")
    }
}