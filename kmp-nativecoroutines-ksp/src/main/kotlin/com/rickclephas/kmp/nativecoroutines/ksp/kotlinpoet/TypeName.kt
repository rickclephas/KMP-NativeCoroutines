package com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName

internal fun KSClassDeclaration.toTypeName(
    typeParameterResolver: TypeParameterResolver
): TypeName {
    val className = toClassName()
    val typeParams = typeParameters
    return when {
        typeParams.isEmpty() -> className
        else -> className.parameterizedBy(typeParams.toTypeVariableNames(typeParameterResolver))
    }
}

internal val TypeName.canonicalClassName: String? get() = when (this) {
    is ClassName -> canonicalName
    is ParameterizedTypeName -> rawType.canonicalName
    else -> null
}
