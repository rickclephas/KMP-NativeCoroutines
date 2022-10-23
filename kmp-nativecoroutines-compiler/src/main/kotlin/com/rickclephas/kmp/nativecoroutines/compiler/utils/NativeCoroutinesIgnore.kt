package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.name.FqName

private val nativeCoroutinesIgnoreFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore")

internal val Annotated.hasIgnoreAnnotation: Boolean
    get() = annotations.findAnnotation(nativeCoroutinesIgnoreFqName) != null
