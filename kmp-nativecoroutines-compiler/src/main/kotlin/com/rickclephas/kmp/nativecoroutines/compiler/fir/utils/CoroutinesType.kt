package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesType
import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.fullyExpandedClass
import org.jetbrains.kotlin.fir.resolve.isSubclassOf
import org.jetbrains.kotlin.fir.types.*

internal fun FirTypeRef.getCoroutinesType(session: FirSession): CoroutinesType? =
    coneTypeOrNull?.getCoroutinesType(session)

internal fun ConeKotlinType.getCoroutinesType(session: FirSession): CoroutinesType? {
    val functionType = functionTypeKind(session)
    val isFunctionType = functionType == FunctionTypeKind.Function
    val isSuspendFunctionType = functionType == FunctionTypeKind.SuspendFunction
    if (isFunctionType || isSuspendFunctionType) {
        val receiverType = receiverType(session)?.getCoroutinesType(session)
        val valueTypes = valueParameterTypesWithoutReceivers(session).map { it.getCoroutinesType(session) }
        val returnType = returnType(session).getCoroutinesType(session)
        if (!isSuspendFunctionType && receiverType == null && valueTypes.all { it == null } && returnType == null) return null
        return CoroutinesType.Function(isSuspendFunctionType, receiverType, valueTypes, returnType)
    }
    val symbol = (this as? ConeClassLikeType)?.toSymbol(session)?.fullyExpandedClass(session) ?: return null
    return coroutinesTypes.firstNotNullOfOrNull { (lookupTag, returnType) ->
        returnType.takeIf { symbol.isSubclassOf(lookupTag, session, isStrict = false, lookupInterfaces = true) }
    }
}

private val coroutinesTypes = mapOf(
    CoroutinesClassIds.stateFlow.toLookupTag() to CoroutinesType.Flow.State,
    CoroutinesClassIds.flow.toLookupTag() to CoroutinesType.Flow.Generic,
)

internal fun FirCallableDeclaration.isCoroutineScopeProperty(session: FirSession): Boolean {
    if (this !is FirProperty) return false
    val symbol = returnTypeRef.toClassLikeSymbol(session)?.fullyExpandedClass(session) ?: return false
    return symbol.isSubclassOf(CoroutinesClassIds.coroutineScope.toLookupTag(), session, isStrict = false, lookupInterfaces = true)
}