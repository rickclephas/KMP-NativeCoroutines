package com.rickclephas.kmp.nativecoroutines

import kotlin.random.Random

/**
 * An exception with a message consisting of 20 random capital letter.
 */
internal class RandomException: Exception(
    (1..20).map { Random.nextInt(65, 91).toChar() }.joinToString("")
)