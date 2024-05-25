package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.isMarkedNullable

internal fun CallableSignature.Type.toNativeConeKotlinType(
    session: FirSession,
    isSuspend: Boolean = false
): ConeKotlinType {
    var type = type
    if (this is CallableSignature.Type.Flow) {
        val typeArgs = arrayOf<ConeTypeProjection>(valueType)
        type = ClassIds.nativeFlow.createConeType(session, typeArgs, type.isMarkedNullable)
    }
    if (isSuspend) {
        val typeArgs = arrayOf<ConeTypeProjection>(type)
        type = ClassIds.nativeSuspend.createConeType(session, typeArgs, type.isMarkedNullable)
    }
    return type
}
