package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

internal val SUFFIX_KEY = CompilerConfigurationKey<String>("suffix")

internal val nativeSuspendFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeSuspend")
internal val nativeSuspendClassId = ClassId.topLevel(nativeSuspendFqName)
internal val nativeSuspendFuncFqName = FqName("com.rickclephas.kmp.nativecoroutines.nativeSuspend")