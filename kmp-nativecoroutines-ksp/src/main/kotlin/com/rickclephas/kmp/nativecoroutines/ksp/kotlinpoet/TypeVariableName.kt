package com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet

import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName

internal fun List<KSTypeParameter>.toTypeVariableNames(
    typeParameterResolver: TypeParameterResolver
): List<TypeVariableName> = map { it.toTypeVariableName(typeParameterResolver) }
