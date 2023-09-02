package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal const val RETURN_TYPE_KOTLIN_SUSPEND = "kotlin-suspend"

/**
 * A function that awaits a suspend function via callbacks.
 *
 * The function takes an `onResult`, `onError` and `onCancelled` callback
 * and returns a cancellable that can be used to cancel the suspend function.
 *
 * When `returnType` isn't `null` the returned cancellable will return the requested type,
 * or `null` if the requested type isn't supported by this [NativeSuspend].
 */
public typealias NativeSuspend<T> = (
    returnType: String?,
    onResult: NativeCallback<T>,
    onError: NativeCallback<NativeError>,
    onCancelled: NativeCallback<NativeError>
) -> NativeCancellable

/**
 * Creates a [NativeSuspend] for the provided suspend [block].
 *
 * @param scope the [CoroutineScope] to run the [block] in, or `null` to use the [defaultCoroutineScope].
 * @param block the suspend-block to await.
 */
public fun <T> nativeSuspend(scope: CoroutineScope? = null, block: suspend () -> T): NativeSuspend<T> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ returnType: String?,
                      onResult: NativeCallback<T>,
                      onError: NativeCallback<NativeError>,
                      onCancelled: NativeCallback<NativeError> ->
        if (returnType == RETURN_TYPE_KOTLIN_SUSPEND) {
            return@collect { block }
        } else if (returnType != null) {
            return@collect { null }
        }
        val job = coroutineScope.launch {
            try {
                onResult(block())
            } catch (e: CancellationException) {
                // CancellationExceptions are handled by the invokeOnCompletion
                // this is required since the job could be cancelled before it is started
                throw e
            } catch (e: Throwable) {
                onError(e.asNativeError())
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
 * Invokes and awaits the result of this [NativeSuspend], converting it to a suspend function.
 *
 * @see suspendCancellableCoroutine
 */
public suspend fun <T> NativeSuspend<T>.await(): T {
    val block = invoke(RETURN_TYPE_KOTLIN_SUSPEND, ::EmptyNativeCallback, ::EmptyNativeCallback, ::EmptyNativeCallback)()
    if (block != null) {
        @Suppress("UNCHECKED_CAST")
        return (block as (suspend () -> T))()
    }
    return suspendCancellableCoroutine { cont ->
        val cancellable = invoke(
            null,
            { result, unit ->
                cont.resume(result)
                unit
            },
            { error, unit -> // TODO: Convert native error
                cont.resumeWithException(RuntimeException("NSError: $error"))
                unit
            },
            { _, unit ->  // TODO: Convert native error
                cont.cancel()
                unit
            }
        )
        cont.invokeOnCancellation { cancellable() }
    }
}
