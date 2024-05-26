package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.ConeKotlinTypeProjection
import org.jetbrains.kotlin.fir.types.ConeStarProjection
import org.jetbrains.kotlin.name.StandardClassIds

internal fun FirCallableSymbol<*>.getCallableSignature(session: FirSession): CallableSignature? {
    val returnType = resolvedReturnTypeRefOrNull?.type?.fullyExpandedType(session) ?: return null
    val valueParameters = when (this) {
        is FirFunctionSymbol<*> -> valueParameterSymbols.map {
            it.name to it.resolvedReturnType.asRawType()
        }
        else -> emptyList()
    }
    return CallableSignature(
        callableId,
        rawStatus.isSuspend,
        valueParameters,
        returnType.getCallableSignatureType(session)
    )
}

private fun ConeKotlinType.getCallableSignatureType(session: FirSession): CallableSignature.Type {
    if (this !is ConeClassLikeType) return this.asRawType()
    val types = sequence {
        yield(this@getCallableSignatureType)
        val symbol = lookupTag.toSymbol(session) ?: return@sequence
        yieldAll(lookupSuperTypes(symbol, lookupInterfaces = true, deep = true, session))
    }
    for (type in types) {
        when (val classId = type.lookupTag.classId) {
            ClassIds.stateFlow, ClassIds.mutableStateFlow -> {
                val valueType = type.getFlowValueType(session).asRawType()
                val isMutable = classId == ClassIds.mutableStateFlow
                return CallableSignature.Type.Flow.State(this.asRawType(), valueType, isMutable)
            }
            ClassIds.sharedFlow -> {
                val valueType = type.getFlowValueType(session).asRawType()
                return CallableSignature.Type.Flow.Shared(this.asRawType(), valueType)
            }
            ClassIds.flow -> {
                val valueType = type.getFlowValueType(session).asRawType()
                return CallableSignature.Type.Flow.Simple(this.asRawType(), valueType)
            }
        }
    }
    return this.asRawType()
}

private fun ConeKotlinType.getFlowValueType(session: FirSession): ConeKotlinType {
    return when (val argument = typeArguments.firstOrNull()) {
        is ConeKotlinTypeProjection -> argument.type
        is ConeStarProjection, null -> StandardClassIds.Any.createConeType(session, nullable = true)
    }
}

private fun ConeKotlinType.asRawType(): CallableSignature.Type.Raw =
    CallableSignature.Type.Raw.Simple(this)
