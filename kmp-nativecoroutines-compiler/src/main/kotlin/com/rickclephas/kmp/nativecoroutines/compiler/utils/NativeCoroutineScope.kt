package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.name.FqName

private val nativeCoroutineScopeFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope")

val IrProperty.isNativeCoroutineScope: Boolean
    get() = getter?.returnType?.isCoroutineScopeType == true &&
            annotations.any { it.type.classFqName == nativeCoroutineScopeFqName }