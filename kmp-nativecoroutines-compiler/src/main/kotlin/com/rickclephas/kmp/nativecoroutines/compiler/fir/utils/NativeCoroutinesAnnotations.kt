package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation

internal fun FirAnnotationContainer.getNativeCoroutinesAnnotations(
    session: FirSession
): Map<NativeCoroutinesAnnotation, FirAnnotation> = buildMap {
    for (annotation in annotations) {
        val classId = annotation.toAnnotationClassId(session) ?: continue
        val nativeCoroutinesAnnotation = NativeCoroutinesAnnotation.forClassId(classId) ?: continue
        put(nativeCoroutinesAnnotation, annotation)
    }
}
