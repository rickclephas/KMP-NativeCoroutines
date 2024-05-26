package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.FqName

internal object FqNames {
    val throws = FqName("kotlin.Throws")

    val hidesFromObjC = FqName("kotlin.native.HidesFromObjC")
    val objCName = FqName("kotlin.native.ObjCName")
    val refinesInSwift = FqName("kotlin.native.RefinesInSwift")
    val shouldRefineInSwift = FqName("kotlin.native.ShouldRefineInSwift")

    val coroutineScope = FqName("kotlinx.coroutines.CoroutineScope")
    val flow = FqName("kotlinx.coroutines.flow.Flow")
    val stateFlow = FqName("kotlinx.coroutines.flow.StateFlow")

    val nativeCoroutines = FqName("com.rickclephas.kmp.nativecoroutines")
    val nativeFlow = FqName("com.rickclephas.kmp.nativecoroutines.NativeFlow")
    val nativeSuspend = FqName("com.rickclephas.kmp.nativecoroutines.NativeSuspend")
}