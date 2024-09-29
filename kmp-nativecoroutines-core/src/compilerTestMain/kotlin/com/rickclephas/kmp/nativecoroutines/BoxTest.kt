package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public class BoxTest internal constructor() {

    private val resultBuilder = StringBuilder()

    public suspend fun <T> collect(nativeFlow: NativeFlow<T>, maxValues: Int? = null) {
        suspendCoroutine { cont ->
            var valueCount = 0
            var cancellable: NativeCancellable? = null
            var cancel = false
            cancellable = nativeFlow(null, { value, next ->
                if (valueCount != 0) resultBuilder.append(",")
                resultBuilder.append(value)
                if (++valueCount == maxValues) {
                    cancel = true
                    cancellable?.invoke()
                }
                next()
            }, { error ->
                if (error != null) {
                    resultBuilder.append(";$error")
                }
                cont.resume(Unit)
                null
            }, { _ ->
                resultBuilder.append(";<cancelled>")
                cont.resume(Unit)
                null
            })
            if (cancel) cancellable()
        }
        resultBuilder.appendLine()
    }

    public suspend fun <T> await(nativeSuspend: NativeSuspend<T>) {
        suspendCoroutine { cont ->
            nativeSuspend(null, { result ->
                resultBuilder.append(result)
                cont.resume(Unit)
                null
            }, { error ->
                resultBuilder.append(";$error")
                cont.resume(Unit)
                null
            }, { _ ->
                resultBuilder.append(";<cancelled>")
                cont.resume(Unit)
                null
            })
        }
        resultBuilder.appendLine()
    }

    public suspend fun <T> awaitAndCollect(
        nativeSuspendFlow: NativeSuspend<NativeFlow<T>>,
        maxValues: Int? = null
    ) {
        val nativeFlow = suspendCoroutine<NativeFlow<T>> { cont ->
            nativeSuspendFlow(null, { result ->
                cont.resume(result)
                null
            }, { error ->
                cont.resumeWithException(error)
                null
            }, { error ->
                cont.resumeWithException(error)
                null
            })
        }
        collect(nativeFlow, maxValues)
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
