package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.fullyExpandedClass
import org.jetbrains.kotlin.fir.resolve.isSubclassOf
import org.jetbrains.kotlin.fir.types.toLookupTag

internal fun FirCallableDeclaration.getCoroutinesReturnType(session: FirSession): CoroutinesReturnType? {
    val symbol = returnTypeRef.toClassLikeSymbol(session)?.fullyExpandedClass(session) ?: return null
    return coroutinesReturnTypes.firstNotNullOfOrNull { (lookupTag, returnType) ->
        returnType.takeIf { symbol.isSubclassOf(lookupTag, session, isStrict = false, lookupInterfaces = true) }
    }
}

private val coroutinesReturnTypes = mapOf(
    CoroutinesClassIds.stateFlow.toLookupTag() to CoroutinesReturnType.Flow.State,
    CoroutinesClassIds.flow.toLookupTag() to CoroutinesReturnType.Flow.Generic,
)

internal fun FirCallableDeclaration.isCoroutineScopeProperty(session: FirSession): Boolean {
    if (this !is FirProperty) return false
    val symbol = returnTypeRef.toClassLikeSymbol(session)?.fullyExpandedClass(session) ?: return false
    return symbol.isSubclassOf(CoroutinesClassIds.coroutineScope.toLookupTag(), session, isStrict = false, lookupInterfaces = true)
}
