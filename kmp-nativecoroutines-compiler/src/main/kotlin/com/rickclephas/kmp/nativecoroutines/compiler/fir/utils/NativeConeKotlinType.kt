package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.config.SwiftExport
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.constructClassLikeType

internal fun FirCallableSignature.getNativeType(
    type: CallableSignature.Type,
    swiftExport: Set<SwiftExport>,
    isSuspend: Boolean = false,
): ConeKotlinType {
    var nativeType = getRawType(type)
    if (SwiftExport.NO_FUNC_RETURN_TYPES !in swiftExport) {
        if (type is CallableSignature.Type.Flow) {
            val typeArgs = arrayOf<ConeTypeProjection>(getRawType(type.valueType))
            nativeType = ClassIds.nativeFlow.constructClassLikeType(typeArgs, type.isNullable)
        }
        if (isSuspend) {
            val typeArgs = arrayOf<ConeTypeProjection>(nativeType)
            nativeType = ClassIds.nativeSuspend.constructClassLikeType(typeArgs)
        }
    }
    return nativeType
}
