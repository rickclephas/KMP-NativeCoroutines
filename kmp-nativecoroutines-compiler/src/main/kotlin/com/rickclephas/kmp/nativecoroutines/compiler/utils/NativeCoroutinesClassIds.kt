package com.rickclephas.kmp.nativecoroutines.compiler.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesFqNames as FqNames
import org.jetbrains.kotlin.name.ClassId

public object NativeCoroutinesClassIds {
    public val nativeCoroutines: ClassId = ClassId.topLevel(FqNames.nativeCoroutines)
    public val nativeCoroutineScope: ClassId = ClassId.topLevel(FqNames.nativeCoroutineScope)
    public val nativeCoroutinesIgnore: ClassId = ClassId.topLevel(FqNames.nativeCoroutinesIgnore)
    public val nativeCoroutinesRefined: ClassId = ClassId.topLevel(FqNames.nativeCoroutinesRefined)
    public val nativeCoroutinesRefinedState: ClassId = ClassId.topLevel(FqNames.nativeCoroutinesRefinedState)
    public val nativeCoroutinesState: ClassId = ClassId.topLevel(FqNames.nativeCoroutinesState)
}
