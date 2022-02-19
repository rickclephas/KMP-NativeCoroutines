package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * A function that collects a [Flow] via callbacks.
 *
 * The function takes an `onItem` and `onComplete` callback
 * and returns a cancellable that can be used to cancel the collection.
 */
typealias NativeFlow<T> = (onItem: NativeCallback<T>, onComplete: NativeCallback<NativeError?>) -> NativeCancellable

/**
 * Creates a [NativeFlow] for this [Flow].
 *
 * @param scope the [CoroutineScope] to use for the collection, or `null` to use the [defaultCoroutineScope].
 * @param propagatedExceptions an array of [Throwable] types that should be propagated as [NSError]s.
 * @receiver the [Flow] to collect.
 * @see Flow.collect
 */
fun <T> Flow<T>.asNativeFlow(
    scope: CoroutineScope? = null,
    propagatedExceptions: Array<KClass<out Throwable>> = arrayOf()
): NativeFlow<T> {
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
                onComplete(e.asNSError(propagatedExceptions))
            }
        }
        job.invokeOnCompletion { cause ->
            // Only handle CancellationExceptions, all other exceptions should be handled inside the job
            if (cause !is CancellationException) return@invokeOnCompletion
            onComplete(cause.asNSError(propagatedExceptions))
        }
        return@collect job.asNativeCancellable()
    }).freeze()
}
