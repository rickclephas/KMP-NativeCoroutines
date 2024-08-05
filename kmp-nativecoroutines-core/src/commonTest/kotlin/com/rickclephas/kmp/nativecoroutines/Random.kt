package com.rickclephas.kmp.nativecoroutines

import kotlin.random.Random

/**
 * Returns a random string of [length] consisting of capital letters.
 */
internal fun Random.nextString(length: Int): String =
    (1..length).map { nextInt(65, 91).toChar() }.joinToString()
