package com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet

import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec

internal fun Sequence<KSAnnotation>.toAnnotationSpecs(
    ignoredAnnotationNames: Set<String> = emptySet()
): List<AnnotationSpec> = map { it.toAnnotationSpec() }
    .filterNot { it.typeName.canonicalClassName in ignoredAnnotationNames }
    .toList()
