package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val RETURN_TYPE_KOTLIN_FLOW = "kotlin-flow"

/**
 * A function that collects a [Flow] via callbacks.
 *
 * The function takes an `onItem`, `onComplete` and `onCancelled` callback
 * and returns a cancellable that can be used to cancel the collection.
 */
public typealias NativeFlow<T> = (
    returnType: String?,
    onItem: NativeCallback2<T, () -> NativeUnit>,
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
public fun <T> Flow<T>.asNativeFlow(scope: CoroutineScope? = null): NativeFlow<T> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ returnType: String?,
                      onItem: NativeCallback2<T, () -> NativeUnit>,
                      onComplete: NativeCallback<NativeError?>,
                      onCancelled: NativeCallback<NativeError> ->
        if (returnType == RETURN_TYPE_KOTLIN_FLOW) {
            return@collect { this }
        } else if (returnType != null) {
            return@collect { null }
        }
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

/**
 * Creates a cold [Flow] for this [NativeFlow].
 *
 * @see callbackFlow
 */
public fun <T> NativeFlow<T>.asFlow(): Flow<T> {
    val flow = invoke(RETURN_TYPE_KOTLIN_FLOW, ::EmptyNativeCallback2, ::EmptyNativeCallback, ::EmptyNativeCallback)()
    if (flow != null) {
        @Suppress("UNCHECKED_CAST")
        return (flow as Flow<T>)
    }
    return callbackFlow {
        val cancellable = invoke(
            null,
            { value, _, unit ->
                trySendBlocking(value)
                unit
            },
            { error, unit ->  // TODO: Convert native error
                close(error?.let { RuntimeException("NSError: $it") })
                unit
            },
            { _, unit -> // TODO: Convert native error
                cancel()
                unit
            }
        )
        awaitClose { cancellable() }
    }
}
