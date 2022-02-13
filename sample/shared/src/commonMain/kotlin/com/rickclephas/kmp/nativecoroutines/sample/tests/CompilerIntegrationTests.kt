package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore

class CompilerIntegrationTests<V>: IntegrationTests() {

    suspend fun returnGenericClassValue(value: V): V {
        return value
    }

    suspend fun returnDefaultValue(value: Int = 1): Int {
        return value
    }

    suspend fun <T> returnGenericValue(value: T): T {
        return value
    }

    fun returnAppendable(value: String): Appendable = StringBuilder(value)

    suspend fun <T: Appendable> returnConstrainedGenericValue(value: T): T {
        return value
    }

    suspend fun <T> returnGenericValues(values: List<T>): List<T> {
        return values
    }

    suspend fun <T> returnGenericVarargValues(vararg values: T): Array<out T> {
        return values
    }

    suspend fun <T> List<T>.returnGenericValueFromExtension(value: T): T {
        return value
    }

    @NativeCoroutinesIgnore
    suspend fun returnIgnoredValue(value: Int): Int {
        return value
    }
}