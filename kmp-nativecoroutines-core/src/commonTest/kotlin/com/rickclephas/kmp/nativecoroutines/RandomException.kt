package com.rickclephas.kmp.nativecoroutines

import kotlin.random.Random

/**
 * An exception with a message consisting of 20 random capital letter.
 */
internal class RandomException: Exception(Random.nextString(20))