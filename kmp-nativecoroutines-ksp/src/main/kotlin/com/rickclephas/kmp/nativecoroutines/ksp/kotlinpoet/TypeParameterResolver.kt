package com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver

internal fun KSDeclaration.getTypeParameterResolver(): TypeParameterResolver =
    typeParameters.toTypeParameterResolver(parentDeclaration?.getTypeParameterResolver())
