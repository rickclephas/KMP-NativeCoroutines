package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A function that collects a [Flow] via callbacks.
 *
 * The function takes an `onItem` and `onComplete` callback
 * and returns a cancellable that can be used to cancel the collection.
 */
typealias NativeFlow<T, Unit> = (
    onItem: NativeCallback<T>,
    onComplete: NativeCallback<NSError?>
) -> NativeCancellable<Unit>

/**
 * Creates a [NativeFlow] for this [Flow].
 *
 * @param scope the [CoroutineScope] to use for the collection, or `null` to use the [defaultCoroutineScope].
 * @receiver the [Flow] to collect.
 * @see Flow.collect
 */
fun <T> Flow<T>.asNativeFlow(scope: CoroutineScope? = null): NativeFlow<T, Unit> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ onItem: NativeCallback<T>, onComplete: NativeCallback<NativeError?> ->
        val job = coroutineScope.launch {
            try {
                collect { onItem(it) }
                onComplete(null)
            } catch (e: CancellationException) {
                // CancellationExceptions are handled by the invokeOnCompletion
                // this is required since the job could be cancelled before it is started
                throw e
            }  catch (e: Throwable) {
                onComplete(e.asNativeError())
            }
        }
        job.invokeOnCompletion { cause ->
            // Only handle CancellationExceptions, all other exceptions should be handled inside the job
            if (cause !is CancellationException) return@invokeOnCompletion
            onComplete(cause.asNativeError())
        }
        return@collect job.asNativeCancellable()
    }).freeze()
}

/**
 * Creates a cold [Flow] for this [NativeFlow].
 *
 * @see callbackFlow
 */
fun <T, Unit> NativeFlow<T, Unit>.asFlow(): Flow<T> = callbackFlow {
    val cancellable = invoke(
        { value, _ -> trySendBlocking(value) },
        { error, _ -> close(error?.let { RuntimeException("NSError: $it") }) } // TODO: Convert NSError
    )
    awaitClose { cancellable() }
}
