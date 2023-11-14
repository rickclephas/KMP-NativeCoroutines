package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesClassIds as ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId

internal class NativeCoroutinesAnnotations(
    declaration: FirCallableDeclaration,
    session: FirSession
) {
    val nativeCoroutines = declaration.getAnnotationByClassId(ClassIds.nativeCoroutines, session)
    val nativeCoroutineScope = declaration.getAnnotationByClassId(ClassIds.nativeCoroutineScope, session)
    val nativeCoroutinesIgnore = declaration.getAnnotationByClassId(ClassIds.nativeCoroutinesIgnore, session)
    val nativeCoroutinesRefined = declaration.getAnnotationByClassId(ClassIds.nativeCoroutinesRefined, session)
    val nativeCoroutinesRefinedState = declaration.getAnnotationByClassId(ClassIds.nativeCoroutinesRefinedState, session)
    val nativeCoroutinesState = declaration.getAnnotationByClassId(ClassIds.nativeCoroutinesState, session)
}
