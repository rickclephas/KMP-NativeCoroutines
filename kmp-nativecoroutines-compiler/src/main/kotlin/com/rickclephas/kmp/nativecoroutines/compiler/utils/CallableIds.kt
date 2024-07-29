package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object CallableIds {
    val todo = CallableId(FqNames.kotlin, Name.identifier("TODO"))

    val asNativeFlow = CallableId(FqNames.nativeCoroutines, Name.identifier("asNativeFlow"))
    val nativeSuspend = CallableId(FqNames.nativeCoroutines, Name.identifier("nativeSuspend"))

    val sharedFlowReplayCache = CallableId(ClassIds.sharedFlow, Name.identifier("replayCache"))
    val stateFlowValue = CallableId(ClassIds.stateFlow, Name.identifier("value"))
    val mutableStateFlowValue = CallableId(ClassIds.mutableStateFlow, Name.identifier("value"))

    val observableViewModelScope = CallableId(ClassIds.observableViewModel, Name.identifier("viewModelScope"))
    val observableCoroutineScope = CallableId(FqName("com.rickclephas.kmp.observableviewmodel"), Name.identifier("coroutineScope"))
    val androidxViewModelScope = CallableId(FqName("androidx.lifecycle"), Name.identifier("viewModelScope"))
}
