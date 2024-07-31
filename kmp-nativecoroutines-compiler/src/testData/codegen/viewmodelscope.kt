// FILE: androidxviewmodel.kt

package androidx.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class ViewModel

val ViewModel.viewModelScope: CoroutineScope
    get() = CoroutineScope(Dispatchers.Default)

// FILE: observableviewmodel.kt

package com.rickclephas.kmp.observableviewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ViewModelScope

val ViewModelScope.coroutineScope: CoroutineScope
    get() = CoroutineScope(Dispatchers.Default)

open class ViewModel: androidx.lifecycle.ViewModel() {
    public val viewModelScope: ViewModelScope = ViewModelScope()
}

// FILE: viewmodelscope.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.runBoxTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@NativeCoroutineScope
internal val unusedCoroutineScope = CoroutineScope(Dispatchers.Default)

class MyAndroidXViewModel1: androidx.lifecycle.ViewModel() {
    @NativeCoroutines
    suspend fun returnSuspendValue1(): String = "OK1"
}

@NativeCoroutines
suspend fun MyAndroidXViewModel1.returnSuspendValue2(): String = "OK2"

class MyObservableViewModel1: com.rickclephas.kmp.observableviewmodel.ViewModel() {
    @NativeCoroutines
    suspend fun returnSuspendValue1(): String = "OK3"
}

@NativeCoroutines
suspend fun MyObservableViewModel1.returnSuspendValue2(): String = "OK4"

class MyAndroidXViewModel2: androidx.lifecycle.ViewModel() {

    @NativeCoroutineScope
    internal val customCoroutineScope = CoroutineScope(Dispatchers.Default)

    @NativeCoroutines
    suspend fun returnSuspendValue1(): String = "OK5"
}

@NativeCoroutines
suspend fun MyAndroidXViewModel2.returnSuspendValue2(): String = "OK6"

class MyObservableViewModel2: com.rickclephas.kmp.observableviewmodel.ViewModel() {

    @NativeCoroutineScope
    internal val customCoroutineScope = CoroutineScope(Dispatchers.Default)

    @NativeCoroutines
    suspend fun returnSuspendValue1(): String = "OK7"
}

@NativeCoroutines
suspend fun MyObservableViewModel2.returnSuspendValue2(): String = "OK8"

fun box() = runBoxTest {
    await(MyAndroidXViewModel1().returnSuspendValue1Native())
    await(MyAndroidXViewModel1().returnSuspendValue2Native())
    await(MyObservableViewModel1().returnSuspendValue1Native())
    await(MyObservableViewModel1().returnSuspendValue2Native())
    await(MyAndroidXViewModel2().returnSuspendValue1Native())
    await(MyAndroidXViewModel2().returnSuspendValue2Native())
    await(MyObservableViewModel2().returnSuspendValue1Native())
    await(MyObservableViewModel2().returnSuspendValue2Native())
}
