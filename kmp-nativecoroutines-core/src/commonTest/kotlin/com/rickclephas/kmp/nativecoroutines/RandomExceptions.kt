package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlin.random.Random

/**
 * An [Exception] with a message consisting of 20 random capital letter.
 */
internal class RandomException: Exception(Random.nextString(20))

/**
 * A [CancellationException] with a message consisting of 20 random capital letter.
 */
internal class RandomCancellationException: CancellationException(Random.nextString(20))
