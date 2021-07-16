package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.FqName

private val coroutineScopeFqName = FqName("kotlinx.coroutines.CoroutineScope")

internal val IrType.isCoroutineScopeType: Boolean
    get() = classFqName == coroutineScopeFqName || superTypes().any { it.isCoroutineScopeType }