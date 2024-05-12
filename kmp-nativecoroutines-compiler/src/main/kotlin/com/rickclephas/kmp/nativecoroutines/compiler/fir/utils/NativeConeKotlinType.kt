package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.isMarkedNullable

internal fun ConeKotlinType.getNativeConeKotlinType(session: FirSession, isSuspend: Boolean = false): ConeKotlinType {
    var type = this
    val coroutinesType = type.getCoroutinesType(session)
    if (coroutinesType is CoroutinesType.Flow) {
        val typeArgs = arrayOf<ConeTypeProjection>(coroutinesType.valueType)
        type = ClassIds.nativeFlow.createConeType(session, typeArgs, type.isMarkedNullable)
    }
    if (isSuspend) {
        val typeArgs = arrayOf<ConeTypeProjection>(type)
        type = ClassIds.nativeSuspend.createConeType(session, typeArgs, type.isMarkedNullable)
    }
    return type
}
