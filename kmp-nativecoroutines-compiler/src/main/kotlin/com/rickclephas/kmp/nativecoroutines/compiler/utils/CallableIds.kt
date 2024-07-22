package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

internal object CallableIds {
    val todo = CallableId(FqNames.kotlin, Name.identifier("TODO"))

    val asNativeFlow = CallableId(FqNames.nativeCoroutines, Name.identifier("asNativeFlow"))
    val nativeSuspend = CallableId(FqNames.nativeCoroutines, Name.identifier("nativeSuspend"))

    val stateFlowValue = CallableId(ClassIds.stateFlow, Name.identifier("value"))
    val mutableStateFlowValue = CallableId(ClassIds.mutableStateFlow, Name.identifier("value"))
}
