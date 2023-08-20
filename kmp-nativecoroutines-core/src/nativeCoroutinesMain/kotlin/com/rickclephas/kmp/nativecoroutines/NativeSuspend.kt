package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.native.concurrent.freeze

/**
 * A function that awaits a suspend function via callbacks.
 *
 * The function takes an `onResult`, `onError` and `onCancelled` callback
 * and returns a cancellable that can be used to cancel the suspend function.
 */
public typealias NativeSuspend<T, Unit> = (
    onResult: NativeCallback<T>,
    onError: NativeCallback<NativeError>,
    onCancelled: NativeCallback<NativeError>
) -> NativeCancellable<Unit>

/**
 * Creates a [NativeSuspend] for the provided suspend [block].
 *
 * @param scope the [CoroutineScope] to run the [block] in, or `null` to use the [defaultCoroutineScope].
 * @param block the suspend-block to await.
 */
public fun <T> nativeSuspend(scope: CoroutineScope? = null, block: suspend () -> T): NativeSuspend<T, Unit> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ onResult: NativeCallback<T>,
                      onError: NativeCallback<NativeError>,
                      onCancelled: NativeCallback<NativeError> ->
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
suspend fun <T, Unit> NativeSuspend<T, Unit>.await(): T = suspendCancellableCoroutine { cont ->
    val cancellable = invoke(
        { result, _ -> cont.resume(result) },
        { error, _ -> cont.resumeWithException(RuntimeException("NSError: $error")) } // TODO: Convert NSError
    )
    cont.invokeOnCancellation { cancellable() }
}
