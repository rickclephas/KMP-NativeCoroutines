package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.GeneratedDeclarationKey

internal data class NativeCoroutinesDeclarationKey(
    val annotation: NativeCoroutinesAnnotation,
    val callableSignature: CallableSignature
): GeneratedDeclarationKey()
