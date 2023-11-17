package com.rickclephas.kmp.nativecoroutines.compiler.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesFqNames as FqNames
import org.jetbrains.kotlin.name.ClassId

internal object CoroutinesClassIds {
    val coroutineScope: ClassId = ClassId.topLevel(FqNames.coroutineScope)
    val flow: ClassId = ClassId.topLevel(FqNames.flow)
    val stateFlow: ClassId = ClassId.topLevel(FqNames.stateFlow)
}
