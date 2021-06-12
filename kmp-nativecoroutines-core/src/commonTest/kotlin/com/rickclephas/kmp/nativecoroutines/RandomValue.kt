package com.rickclephas.kmp.nativecoroutines

import kotlin.random.Random

/**
 * A data class containing a random integer.
 */
internal data class RandomValue(
    val randomInt: Int = Random.nextInt()
)
