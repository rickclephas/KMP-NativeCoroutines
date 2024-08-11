package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeTypeProjection
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.utils.addIfNotNull

internal fun FirCallableSignature.getNativeType(
    type: CallableSignature.Type,
    isSuspend: Boolean = false
): ConeKotlinType {
    val nativeType = when (type) {
        is CallableSignature.Type.Flow -> {
            val valueType = getRawType(type.valueType).orNativeUnit
            val typeArgs = arrayOf<ConeTypeProjection>(valueType)
            ClassIds.nativeFlow.constructClassLikeType(typeArgs, type.isNullable)
        }
        is CallableSignature.Type.Function -> {
            val typeArguments = buildList {
                addIfNotNull(type.receiverType?.let(::getNativeType))
                addAll(type.valueTypes.map(::getNativeType))
                add(getNativeType(type.returnType, type.isSuspend))
            }
            val arity = typeArguments.size - 1
            FunctionTypeKind.Function.numberedClassId(arity)
                .constructClassLikeType(typeArguments.toTypedArray(), isNullable = type.isNullable)
        }
        is CallableSignature.Type.Raw -> getRawType(type)
    }
    if (!isSuspend) return nativeType
    val typeArgs = arrayOf<ConeTypeProjection>(nativeType.orNativeUnit)
    return ClassIds.nativeSuspend.constructClassLikeType(typeArgs)
}

private val ConeKotlinType.orNativeUnit: ConeKotlinType get() = when (isUnit) {
    true -> ClassIds.nativeUnit.constructClassLikeType(isNullable = true)
    false -> this
}
