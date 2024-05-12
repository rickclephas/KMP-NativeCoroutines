package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.ClassId

internal object ClassIds {
    val throws = ClassId.topLevel(FqNames.throws)

    val hidesFromObjC = ClassId.topLevel(FqNames.hidesFromObjC)
    val objCName = ClassId.topLevel(FqNames.objCName)
    val refinesInSwift = ClassId.topLevel(FqNames.refinesInSwift)
    val shouldRefineInSwift = ClassId.topLevel(FqNames.shouldRefineInSwift)

    val coroutineScope = ClassId.topLevel(FqNames.coroutineScope)
    val flow = ClassId.topLevel(FqNames.flow)
    val stateFlow = ClassId.topLevel(FqNames.stateFlow)

    val nativeFlow = ClassId.topLevel(FqNames.nativeFlow)
    val nativeSuspend = ClassId.topLevel(FqNames.nativeSuspend)
}
