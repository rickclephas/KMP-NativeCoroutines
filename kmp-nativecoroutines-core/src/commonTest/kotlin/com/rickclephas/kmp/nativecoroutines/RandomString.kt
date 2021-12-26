package com.rickclephas.kmp.nativecoroutines

import kotlin.random.Random

/**
 * Generates a random string with the specified [length].
 */
internal fun Random.nextString(length: Int = 10) = (1..length).map {
    Random.nextInt(65, 91).toChar()
}.joinToString("")