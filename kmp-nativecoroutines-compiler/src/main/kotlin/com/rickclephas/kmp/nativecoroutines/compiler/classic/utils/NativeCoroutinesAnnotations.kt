package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor

internal fun Annotated.getNativeCoroutinesAnnotations(): Map<NativeCoroutinesAnnotation, AnnotationDescriptor> = buildMap {
    for (annotation in annotations) {
        val fqName = annotation.fqName ?: continue
        val nativeCoroutinesAnnotation = NativeCoroutinesAnnotation.forFqName(fqName) ?: continue
        put(nativeCoroutinesAnnotation, annotation)
    }
}

internal val AnnotationDescriptor.isNativeCoroutinesAnnotation: Boolean get() {
    val fqName = fqName ?: return false
    return NativeCoroutinesAnnotation.entries.any { it.fqName == fqName }
}
