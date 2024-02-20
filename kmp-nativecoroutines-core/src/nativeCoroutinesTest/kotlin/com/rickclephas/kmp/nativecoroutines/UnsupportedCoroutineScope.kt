package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

/**
 * A [CoroutineScope] that throws an [UnsupportedOperationException] when used.
 */
internal object UnsupportedCoroutineScope: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = throw UnsupportedOperationException("CoroutineScope shouldn't be used")
}
