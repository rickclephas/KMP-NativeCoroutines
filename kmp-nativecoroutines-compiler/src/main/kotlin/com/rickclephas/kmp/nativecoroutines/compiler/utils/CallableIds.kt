package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

internal object CallableIds {
    val asNativeFlow = CallableId(FqNames.nativeCoroutines, Name.identifier("asNativeFlow"))
    val nativeSuspend = CallableId(FqNames.nativeCoroutines, Name.identifier("nativeSuspend"))
}
