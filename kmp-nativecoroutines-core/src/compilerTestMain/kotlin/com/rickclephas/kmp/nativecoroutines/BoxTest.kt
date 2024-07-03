package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public class BoxTest internal constructor() {

    private val resultBuilder = StringBuilder()

    public suspend fun <T> collect(nativeFlow: NativeFlow<T>, maxValues: Int? = null) {
        suspendCoroutine { cont ->
            var valueCount = 0
            var cancellable: NativeCancellable? = null
            cancellable = nativeFlow({ value, next, _ ->
                if (valueCount != 0) resultBuilder.append(",")
                resultBuilder.append(value)
                if (++valueCount == maxValues) {
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
        }
        resultBuilder.appendLine()
    }

    public suspend fun <T> await(nativeSuspend: NativeSuspend<T>) {
        suspendCoroutine { cont ->
            nativeSuspend({ result, _ ->
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

    override fun toString(): String = resultBuilder.toString()
}

public fun runBoxTest(action: suspend BoxTest.() -> Unit): String =
    runBlocking { BoxTest().apply { action() }.toString() }
