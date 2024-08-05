package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.isUnit

internal fun FirCallableSignature.getNativeType(
    type: CallableSignature.Type,
    isSuspend: Boolean = false
): ConeKotlinType {
    var nativeType = getRawType(type)
    if (type is CallableSignature.Type.Flow) {
        val valueType = getRawType(type.valueType).orNativeUnit
        val typeArgs = arrayOf<ConeTypeProjection>(valueType)
        nativeType = ClassIds.nativeFlow.constructClassLikeType(typeArgs, type.isNullable)
    }
    if (isSuspend) {
        val typeArgs = arrayOf<ConeTypeProjection>(nativeType.orNativeUnit)
        nativeType = ClassIds.nativeSuspend.constructClassLikeType(typeArgs)
    }
    return nativeType
}

private val ConeKotlinType.orNativeUnit: ConeKotlinType get() = when (isUnit) {
    true -> ClassIds.nativeUnit.constructClassLikeType(isNullable = true)
    false -> this
}
