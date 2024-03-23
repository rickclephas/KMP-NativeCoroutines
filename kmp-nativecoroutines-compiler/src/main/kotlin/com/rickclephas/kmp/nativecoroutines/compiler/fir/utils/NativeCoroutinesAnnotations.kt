package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol

internal fun FirAnnotationContainer.getNativeCoroutinesAnnotations(
    session: FirSession
): Map<NativeCoroutinesAnnotation, FirAnnotation> = annotations.getNativeCoroutinesAnnotations(session)

internal fun FirBasedSymbol<*>.getNativeCoroutinesAnnotations(
    session: FirSession
): Map<NativeCoroutinesAnnotation, FirAnnotation> = resolvedAnnotationsWithClassIds.getNativeCoroutinesAnnotations(session)

private fun List<FirAnnotation>.getNativeCoroutinesAnnotations(
    session: FirSession
): Map<NativeCoroutinesAnnotation, FirAnnotation> = buildMap {
    for (annotation in this@getNativeCoroutinesAnnotations) {
        val classId = annotation.toAnnotationClassId(session) ?: continue
        val nativeCoroutinesAnnotation = NativeCoroutinesAnnotation.forClassId(classId) ?: continue
        put(nativeCoroutinesAnnotation, annotation)
    }
}
