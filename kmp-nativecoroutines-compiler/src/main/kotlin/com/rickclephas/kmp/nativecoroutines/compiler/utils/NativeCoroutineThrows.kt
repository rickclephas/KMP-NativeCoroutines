package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.util.findAnnotation
import org.jetbrains.kotlin.name.FqName

private val nativeCoroutineThrowsFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutineThrows")

internal fun List<IrConstructorCall>.findNativeCoroutineThrowsAnnotation() =
    findAnnotation(nativeCoroutineThrowsFqName)