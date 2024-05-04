package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.FqName

internal object CoroutinesFqNames {
    val coroutineScope: FqName = FqName("kotlinx.coroutines.CoroutineScope")
    val flow: FqName = FqName("kotlinx.coroutines.flow.Flow")
    val stateFlow: FqName = FqName("kotlinx.coroutines.flow.StateFlow")

    val nativeFlow: FqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeFlow")
    val nativeSuspend: FqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeSuspend")
}
