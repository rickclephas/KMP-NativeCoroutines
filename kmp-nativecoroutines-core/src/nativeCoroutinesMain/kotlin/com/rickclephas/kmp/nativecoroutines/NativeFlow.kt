package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A function that collects a [Flow] via callbacks.
 *
 * The function takes an `onItem` and `onComplete` callback
 * and returns a cancellable that can be used to cancel the collection.
 */
typealias NativeFlow<T> = (
    onItem: NativeCallback2<T, () -> Unit>,
    onComplete: NativeCallback<NativeError?>,
    onCancelled: NativeCallback<NativeError>
) -> NativeCancellable

/**
 * Creates a [NativeFlow] for this [Flow].
 *
 * @param scope the [CoroutineScope] to use for the collection, or `null` to use the [defaultCoroutineScope].
 * @receiver the [Flow] to collect.
 * @see Flow.collect
 */
fun <T> Flow<T>.asNativeFlow(scope: CoroutineScope? = null): NativeFlow<T> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ onItem: NativeCallback2<T, () -> Unit>,
                      onComplete: NativeCallback<NativeError?>,
                      onCancelled: NativeCallback<NativeError> ->
        val job = coroutineScope.launch {
            try {
                collect {
                    suspendCoroutine { cont ->
                        onItem(it) { cont.resume(Unit) }
                    }
                }
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
            onCancelled(cause.asNativeError())
        }
        return@collect job.asNativeCancellable()
    })
}
