package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import kotlin.native.concurrent.freeze
import kotlin.reflect.KClass

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
 * @param propagatedExceptions a list of [Throwable] types that should be propagated as [NSError]s.
 * @param block the suspend block to await.
 */
fun <T> nativeSuspend(
    scope: CoroutineScope? = null,
    propagatedExceptions: List<KClass<out Throwable>> = listOf(CancellationException::class),
    block: suspend () -> T
): NativeSuspend<T> {
    val coroutineScope = scope ?: defaultCoroutineScope
    return (collect@{ onResult: NativeCallback<T>, onError: NativeCallback<NSError> ->
        val job = coroutineScope.launch {
            try {
                onResult(block())
            } catch (e: CancellationException) {
                // CancellationExceptions are handled by the invokeOnCompletion
                // this is required since the job could be cancelled before it is started
                throw e
            } catch (e: Throwable) {
                onError(e.asNSError(propagatedExceptions))
            }
        }
        job.invokeOnCompletion { cause ->
            // Only handle CancellationExceptions, all other exceptions should be handled inside the job
            if (cause !is CancellationException) return@invokeOnCompletion
            onError(cause.asNSError(propagatedExceptions))
        }
        return@collect job.asNativeCancellable()
    }).freeze()
}