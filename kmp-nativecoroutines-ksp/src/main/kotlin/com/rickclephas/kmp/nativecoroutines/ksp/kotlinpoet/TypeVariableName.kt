package com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet

import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun List<KSTypeParameter>.toTypeVariableNames(
    typeParameterResolver: TypeParameterResolver,
    withoutVariance: Boolean = false
): List<TypeVariableName> = map { it.toTypeVariableName(typeParameterResolver, withoutVariance) }

internal fun KSTypeParameter.toTypeVariableName(
    typeParamResolver: TypeParameterResolver,
    withoutVariance: Boolean = false
): TypeVariableName {
    val typeVarName = name.getShortName()
    val typeVarBounds = bounds.map { it.toTypeName(typeParamResolver) }.toList()
    val typeVarVariance = when {
        withoutVariance -> null
        variance == Variance.COVARIANT -> KModifier.OUT
        variance == Variance.CONTRAVARIANT -> KModifier.IN
        else -> null
    }
    return TypeVariableName(typeVarName, bounds = typeVarBounds, variance = typeVarVariance)
}
