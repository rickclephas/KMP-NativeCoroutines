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
    onResult: NativeCallback1<T>,
    onError: NativeCallback1<NativeError>,
    onCancelled: NativeCallback1<NativeError>
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
                      onResult: NativeCallback1<T>,
                      onError: NativeCallback1<NativeError>,
                      onCancelled: NativeCallback1<NativeError> ->
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
 * Creates a [NativeSuspend] for the provided suspend [block].
 *
 * @param scope the [CoroutineScope] to run the [block] in, or `null` to use the [defaultCoroutineScope].
 * @param block the suspend-block to await.
 */
public inline fun nativeSuspend(
    scope: CoroutineScope? = null,
    crossinline block: suspend () -> Unit
): NativeSuspend<NativeUnit?> = nativeSuspend<NativeUnit?>(scope) {
    block()
    null
}

/**
 * Invokes and awaits the result of this [NativeSuspend], converting it to a suspend function.
 *
 * @see suspendCancellableCoroutine
 */
public suspend fun <T> NativeSuspend<T>.await(): T {
    val block = invoke(RETURN_TYPE_KOTLIN_SUSPEND, ::EmptyNativeCallback1, ::EmptyNativeCallback1, ::EmptyNativeCallback1)()
    if (block != null) {
        @Suppress("UNCHECKED_CAST")
        return (block as (suspend () -> T))()
    }
    return suspendCancellableCoroutine { cont ->
        val cancellable = invoke(
            null,
            { result ->
                cont.resume(result)
                null
            },
            { error ->
                cont.resumeWithException(error.asThrowable())
                null
            },
            { error ->
                cont.cancel(error.asCancellationException())
                null
            }
        )
        cont.invokeOnCancellation { cancellable() }
    }
}

/**
 * Invokes and awaits the result of this [NativeSuspend], converting it to a suspend function.
 *
 * @see suspendCancellableCoroutine
 */
public suspend inline fun NativeSuspend<NativeUnit?>.await() {
    await<NativeUnit?>()
}
