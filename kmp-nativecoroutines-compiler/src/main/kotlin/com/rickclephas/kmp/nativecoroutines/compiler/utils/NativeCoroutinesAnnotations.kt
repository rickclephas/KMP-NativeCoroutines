package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

public enum class NativeCoroutinesAnnotation(name: String) {
    NativeCoroutines("NativeCoroutines"),
    NativeCoroutineScope("NativeCoroutineScope"),
    NativeCoroutinesIgnore("NativeCoroutinesIgnore"),
    NativeCoroutinesRefined("NativeCoroutinesRefined"),
    NativeCoroutinesRefinedState("NativeCoroutinesRefinedState"),
    NativeCoroutinesState("NativeCoroutinesState");

    public val fqName: FqName = FqName("com.rickclephas.kmp.nativecoroutines.$name")
    public val classId: ClassId = ClassId.topLevel(fqName)

    public companion object {
        public fun forFqName(fqName: FqName): NativeCoroutinesAnnotation? =
            entries.firstOrNull { it.fqName == fqName }

        public fun forClassId(classId: ClassId): NativeCoroutinesAnnotation? =
            entries.firstOrNull { it.classId == classId }
    }
}
