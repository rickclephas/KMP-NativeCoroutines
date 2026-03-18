package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.*
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalTypeInference

public class BoxTest internal constructor() {

    private val resultBuilder = StringBuilder()

    public suspend fun <T> collect(nativeFlow: NativeFlow<T>, maxValues: Int? = null) {
        suspendCoroutine { cont ->
            var valueCount = 0
            var cancellable: NativeCancellable? = null
            var cancel = false
            cancellable = nativeFlow({ value, next, _ ->
                if (valueCount != 0) resultBuilder.append(",")
                resultBuilder.append(value)
                if (++valueCount == maxValues) {
                    cancel = true
                    cancellable?.invoke()
                }
                next()
            }, { error, _ ->
                if (error != null) {
                    resultBuilder.append(";$error")
                }
                cont.resume(Unit)
            }, { _, _ ->
                resultBuilder.append(";<cancelled>")
                cont.resume(Unit)
            })
            if (cancel) cancellable()
        }
        resultBuilder.appendLine()
    }

    public suspend fun <T> collect(flow: Flow<T>, maxValues: Int? = null) {
        try {
            var valueCount = 0
            flow.collect { value ->
                if (valueCount != 0) resultBuilder.append(",")
                resultBuilder.append(value)
                if (++valueCount == maxValues) {
                    throw CancellationException()
                }
            }
        } catch (_: CancellationException) {
            resultBuilder.append(";<cancelled>")
        } catch (e: Throwable) {
            resultBuilder.append(";$e")
        }
        resultBuilder.appendLine()
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public suspend fun <T> await(nativeSuspend: () -> NativeSuspend<T>) {
        suspendCoroutine { cont ->
            nativeSuspend()({ result, _ ->
                resultBuilder.append(result)
                cont.resume(Unit)
            }, { error, _ ->
                resultBuilder.append(";$error")
                cont.resume(Unit)
            }, { _, _ ->
                resultBuilder.append(";<cancelled>")
                cont.resume(Unit)
            })
        }
        resultBuilder.appendLine()
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public suspend fun <T> await(result: suspend () -> T) {
        try {
            resultBuilder.append(result())
        } catch (_: CancellationException) {
            resultBuilder.append(";<cancelled>")
        } catch (e: Throwable) {
            resultBuilder.append(";$e")
        }
        resultBuilder.appendLine()
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public suspend fun <T> awaitAndCollect(
        maxValues: Int? = null,
        nativeSuspend: () -> NativeSuspend<NativeFlow<T>>
    ) {
        val nativeFlow = suspendCoroutine<NativeFlow<T>> { cont ->
            nativeSuspend()({ result, _ ->
                cont.resume(result)
            }, { error, _ ->
                cont.resumeWithException(error)
            }, { error, _ ->
                cont.resumeWithException(error)
            })
        }
        collect(nativeFlow, maxValues)
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    @JvmName("awaitAndCollectNativeFlow")
    public suspend fun <T> awaitAndCollect(
        maxValues: Int? = null,
        nativeFlow: suspend () -> NativeFlow<T>
    ) {
        collect(nativeFlow(), maxValues)
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public suspend fun <T> awaitAndCollect(
        maxValues: Int? = null,
        flow: suspend () -> Flow<T>
    ) {
        collect(flow(), maxValues)
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public suspend fun <T> awaitAndCollectNull(
        nativeSuspend: () -> NativeSuspend<NativeFlow<T>?>
    ) {
        await<NativeFlow<T>?>(nativeSuspend)
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    @JvmName("awaitAndCollectNullNativeFlow")
    public suspend fun <T> awaitAndCollectNull(
        nativeFlow: suspend () -> NativeFlow<T>?
    ) {
        await<NativeFlow<T>?>(nativeFlow)
    }

    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public suspend fun <T> awaitAndCollectNull(
        flow: suspend () -> Flow<T>?
    ) {
        await<Flow<T>?>(flow)
    }

    public fun <T> value(value: T) {
        resultBuilder.appendLine(value)
    }

    public fun <T> values(values: List<T>) {
        resultBuilder.appendLine(values.joinToString(","))
    }

    override fun toString(): String = resultBuilder.toString()
}

public fun runBoxTest(action: suspend BoxTest.() -> Unit): String =
    runBlocking { BoxTest().apply { action() }.toString() }
