package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CompilerIntegrationTests<V>: IntegrationTests() {

    @NativeCoroutines
    suspend fun returnGenericClassValue(value: V): V {
        return value
    }

    @NativeCoroutines
    suspend fun returnDefaultValue(value: Int = 1): Int {
        return value
    }

    @NativeCoroutines
    suspend fun <T> returnGenericValue(value: T): T {
        return value
    }

    fun returnAppendable(value: String): Appendable = StringBuilder(value)

    @NativeCoroutines
    suspend fun <T: Appendable> returnConstrainedGenericValue(value: T): T {
        return value
    }

    @NativeCoroutines
    suspend fun <T> returnGenericValues(values: List<T>): List<T> {
        return values
    }

    @NativeCoroutines
    suspend fun <T> returnGenericVarargValues(vararg values: T): Array<out T> {
        return values
    }

    @NativeCoroutines
    suspend fun <T> List<T>.returnGenericValueFromExtension(value: T): T {
        return value
    }

    @NativeCoroutines
    fun <T> returnGenericFlow(value: T): Flow<T> = flow {
        emit(value)
    }

    @NativeCoroutinesIgnore
    suspend fun returnIgnoredValue(value: Int): Int {
        return value
    }
}
