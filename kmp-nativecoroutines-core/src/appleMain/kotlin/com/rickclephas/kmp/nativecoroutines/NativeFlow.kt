package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import kotlin.native.concurrent.freeze

/**
 * A function that collects a [Flow] via callbacks.
 *
 * The function takes an `onItem` and `onComplete` callback
 * and returns a cancellable that can be used to cancel the collection.
 */
typealias NativeFlow<T> = (onItem: NativeCallback<T>, onComplete: NativeCallback<NSError?>) -> NativeCancellable

/**
 * Creates a [NativeFlow] for this [Flow].
 *
 * @param scope the [CoroutineScope] to use for the collection, or `null` to use the [defaultCoroutineScope].
 * @receiver the [Flow] to collect.
 * @see Flow.collect
 */
fun <T> Flow<T>.asNativeFlow(scope: CoroutineScope? = null): NativeFlow<T> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ onItem: NativeCallback<T>, onComplete: NativeCallback<NSError?> ->
        val job = coroutineScope.launch {
            try {
                collect { onItem(it.freeze()) }
                onComplete(null)
            } catch (e: Exception) {
                onComplete(e.asNSError())
            }
        }
        return@collect job.asNativeCancellable()
    }).freeze()
}