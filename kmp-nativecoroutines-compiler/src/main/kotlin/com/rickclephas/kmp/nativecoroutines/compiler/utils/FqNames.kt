package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.FqName

internal object FqNames {
    val kotlin = FqName("kotlin")

    val deprecated = FqName("kotlin.Deprecated")

    val hidesFromObjC = FqName("kotlin.native.HidesFromObjC")
    val objCName = FqName("kotlin.native.ObjCName")
    val refinesInSwift = FqName("kotlin.native.RefinesInSwift")
    val shouldRefineInSwift = FqName("kotlin.native.ShouldRefineInSwift")

    val coroutineScope = FqName("kotlinx.coroutines.CoroutineScope")
    val flow = FqName("kotlinx.coroutines.flow.Flow")
    val sharedFlow = FqName("kotlinx.coroutines.flow.SharedFlow")
    val stateFlow = FqName("kotlinx.coroutines.flow.StateFlow")
    val mutableStateFlow = FqName("kotlinx.coroutines.flow.MutableStateFlow")

    val nativeCoroutines = FqName("com.rickclephas.kmp.nativecoroutines")
    val nativeFlow = FqName("com.rickclephas.kmp.nativecoroutines.NativeFlow")
    val nativeSuspend = FqName("com.rickclephas.kmp.nativecoroutines.NativeSuspend")

    val observableViewModel = FqName("com.rickclephas.kmp.observableviewmodel.ViewModel")
    val androidxViewModel = FqName("androidx.lifecycle.ViewModel")
}
