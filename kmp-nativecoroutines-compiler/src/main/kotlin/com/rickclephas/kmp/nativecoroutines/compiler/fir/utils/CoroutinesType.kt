package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.StandardClassIds

internal sealed class CoroutinesType private constructor() {
    sealed class Flow private constructor(): CoroutinesType() {
        abstract val valueType: ConeKotlinType
        data class Generic(override val valueType: ConeKotlinType): Flow()
        data class State(override val valueType: ConeKotlinType): Flow()
    }
}

internal fun ConeKotlinType.getCoroutinesType(session: FirSession): CoroutinesType? {
    if (this !is ConeClassLikeType) return null
    val types = sequence {
        yield(this@getCoroutinesType)
        val symbol = lookupTag.toSymbol(session) ?: return@sequence
        yieldAll(lookupSuperTypes(symbol, lookupInterfaces = true, deep = true, session))
    }
    for (type in types) {
        when (type.lookupTag.classId) {
            ClassIds.stateFlow -> return CoroutinesType.Flow.State(type.getFlowValueType(session))
            ClassIds.flow -> return CoroutinesType.Flow.Generic(type.getFlowValueType(session))
        }
    }
    return null
}

private fun ConeKotlinType.getFlowValueType(session: FirSession): ConeKotlinType {
    return when (val argument = typeArguments.firstOrNull()) {
        is ConeKotlinTypeProjection -> argument.type
        is ConeStarProjection, null -> StandardClassIds.Any.createConeType(session, nullable = true)
    }
}
