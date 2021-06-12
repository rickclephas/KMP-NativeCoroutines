package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import kotlin.native.concurrent.freeze

/**
 * A function that awaits a suspend function via callbacks.
 *
 * The function takes an `onResult` and `onError` callback
 * and returns a cancellable that can be used to cancel the suspend function.
 */
typealias NativeSuspend<T> = (onResult: NativeCallback<T>, onError: NativeCallback<NSError>) -> NativeCancellable

/**
 * Creates a [NativeSuspend] for the provided suspend [block].
 *
 * @param scope the [CoroutineScope] to run the [block] in, or `null` to use the [defaultCoroutineScope].
 * @param block the suspend block to await.
 */
fun <T> nativeSuspend(scope: CoroutineScope? = null, block: suspend () -> T): NativeSuspend<T> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ onResult: NativeCallback<T>, onError: NativeCallback<NSError> ->
        val job = coroutineScope.launch {
            try {
                onResult(block())
            } catch (e: Exception) {
                onError(e.asNSError())
            }
        }
        return@collect job.asNativeCancellable()
    }).freeze()
}