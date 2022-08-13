package com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet

import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun List<KSValueParameter>.toParameterSpecs(
    typeParameterResolver: TypeParameterResolver
) = map { parameter ->
    val name = parameter.name?.asString() ?: ""
    val type = parameter.type.toTypeName(typeParameterResolver)
    val builder = ParameterSpec.builder(name, type)
    builder.addAnnotations(parameter.annotations.toAnnotationSpecs())
    if (parameter.isVararg) {
        builder.addModifiers(KModifier.VARARG)
    }
    // TODO: Add default value once those are exported to ObjC
    builder.build()
}
