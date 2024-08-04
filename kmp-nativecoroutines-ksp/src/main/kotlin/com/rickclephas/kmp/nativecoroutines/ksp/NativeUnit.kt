package com.rickclephas.kmp.nativecoroutines.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT

private val NativeUnit = ClassName("com.rickclephas.kmp.nativecoroutines", "NativeUnit")
    .copy(nullable = true)

internal val TypeName.orNativeUnit: TypeName get() = when (this) {
    UNIT -> NativeUnit
    else -> this
}

internal val TypeName.isNativeUnit: Boolean get() = this == NativeUnit
