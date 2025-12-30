package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassLikeSymbol
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.getContainingDeclaration

internal fun FirCallableDeclaration.isRefined(session: FirSession): Boolean =
    annotations.any { annotation ->
        !annotation.isNativeCoroutinesAnnotation(session) && annotation.isRefinementAnnotation(session)
    } || (getContainingClass()?.isHiddenFromObjC(session) ?: false)

private fun FirClassLikeDeclaration.isHiddenFromObjC(session: FirSession): Boolean =
    annotations.any { annotation ->
        annotation.isHidingFromObjCAnnotation(session)
    } || (getContainingDeclaration(session)?.isHiddenFromObjC(session) ?: false)

private fun FirAnnotation.isRefinementAnnotation(session: FirSession): Boolean =
    toAnnotationClassLikeSymbol(session)?.resolvedAnnotationsWithClassIds.orEmpty().any { metaAnnotation ->
        val classId = metaAnnotation.toAnnotationClassId(session)
        classId == ClassIds.hidesFromObjC || classId == ClassIds.refinesInSwift
    }

private fun FirAnnotation.isHidingFromObjCAnnotation(session: FirSession): Boolean =
    toAnnotationClassLikeSymbol(session)?.resolvedAnnotationsWithClassIds.orEmpty().any { metaAnnotation ->
        val classId = metaAnnotation.toAnnotationClassId(session)
        classId == ClassIds.hidesFromObjC
    }
