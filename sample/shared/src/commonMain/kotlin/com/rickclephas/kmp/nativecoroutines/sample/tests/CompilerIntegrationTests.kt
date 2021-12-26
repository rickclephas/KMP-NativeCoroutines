package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineThrows
import com.rickclephas.kmp.nativecoroutines.sample.utils.ClassTestException
import com.rickclephas.kmp.nativecoroutines.sample.utils.ModuleTestException
import com.rickclephas.kmp.nativecoroutines.sample.utils.TestException
import com.rickclephas.kmp.nativecoroutines.sample.utils.freeze
import kotlinx.coroutines.flow.*
import kotlin.coroutines.cancellation.CancellationException

@NativeCoroutineThrows(ClassTestException::class)
class CompilerIntegrationTests<V>: IntegrationTests() {

    @Throws(TestException::class, CancellationException::class)
    suspend fun throwWithThrows() {
        throw TestException()
    }

    @NativeCoroutineThrows(TestException::class)
    suspend fun throwWithNativeCoroutineThrows() {
        throw TestException()
    }

    suspend fun throwWithNativeCoroutineThrowsOnClass() {
        throw ClassTestException()
    }

    suspend fun throwWithPropagatedExceptionInModule() {
        throw ModuleTestException()
    }

    @get:NativeCoroutineThrows(TestException::class)
    val flowThrow: Flow<Int> = flow {
        throw TestException()
    }

    val stateFlow: StateFlow<Int> = MutableStateFlow(1)

    val sharedFlow: SharedFlow<Int> = MutableSharedFlow<Int>(2).apply {
        tryEmit(1)
        tryEmit(2)
    }

    suspend fun returnGenericClassValue(value: V): V {
        return value
    }

    suspend fun returnDefaultValue(value: Int = 1): Int {
        return value
    }

    suspend fun <T> returnGenericValue(value: T): T {
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

    init {
        freeze()
    }
}