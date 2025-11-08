package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Suppress("RedundantSuspendModifier")
public class CompilerIntegrationTests<V>: IntegrationTests() {

    @NativeCoroutines
    public suspend fun returnGenericClassValue(value: V): V {
        return value
    }

    @NativeCoroutines
    public suspend fun returnDefaultValue(value: Int = 1): Int {
        return value
    }

    @NativeCoroutines
    public suspend fun <T> returnGenericValue(value: T): T {
        return value
    }

    public fun returnAppendable(value: String): Appendable = StringBuilder(value)

    @NativeCoroutines
    public suspend fun <T: Appendable> returnConstrainedGenericValue(value: T): T {
        return value
    }

    @NativeCoroutines
    public suspend fun <T> returnGenericValues(values: List<T>): List<T> {
        return values
    }

//    @NativeCoroutines
//    public suspend fun <T> returnGenericVarargValues(vararg values: T): Array<out T> {
//        return values
//    }

    @NativeCoroutines
    @Suppress("UnusedReceiverParameter")
    public suspend fun <T> List<T>.returnGenericValueFromExtension(value: T): T {
        return value
    }

    @NativeCoroutines
    public fun <T> returnGenericFlow(value: T): Flow<T> = flow {
        emit(value)
    }

    @NativeCoroutinesIgnore
    public suspend fun returnIgnoredValue(value: Int): Int {
        return value
    }
}
