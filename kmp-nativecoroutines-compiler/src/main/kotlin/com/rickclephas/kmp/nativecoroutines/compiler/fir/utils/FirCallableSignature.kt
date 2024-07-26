package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.StandardClassIds

internal class FirCallableSignature(
    val signature: CallableSignature,
    private val rawTypes: List<ConeKotlinType>
) {
    fun getRawType(type: CallableSignature.Type): ConeKotlinType = rawTypes[type.rawTypeIndex]

    class Builder(val session: FirSession) {
        private val rawTypes = mutableListOf<ConeKotlinType>()

        private val ConeKotlinType.rawTypeIndex: Int get() {
            rawTypes.add(this)
            return rawTypes.lastIndex
        }

        fun ConeKotlinType.asRawType(): CallableSignature.Type.Raw =
            CallableSignature.Type.Raw(rawTypeIndex)

        fun ConeKotlinType.asSimpleFlow(valueType: CallableSignature.Type): CallableSignature.Type.Flow.Simple =
            CallableSignature.Type.Flow.Simple(rawTypeIndex, valueType)

        fun ConeKotlinType.asSharedFlow(valueType: CallableSignature.Type): CallableSignature.Type.Flow.Shared =
            CallableSignature.Type.Flow.Shared(rawTypeIndex, valueType)

        fun ConeKotlinType.asStateFlow(
            valueType: CallableSignature.Type,
            isMutable: Boolean
        ): CallableSignature.Type.Flow.State = CallableSignature.Type.Flow.State(rawTypeIndex, valueType, isMutable)

        fun build(signature: CallableSignature): FirCallableSignature =
            FirCallableSignature(signature, rawTypes)
    }

    companion object {
        operator fun invoke(session: FirSession, block: Builder.() -> CallableSignature): FirCallableSignature =
            Builder(session).run { build(block()) }
    }
}

internal fun FirCallableSymbol<*>.getCallableSignature(session: FirSession): FirCallableSignature? {
    val returnType = resolvedReturnTypeRefOrNull?.type?.fullyExpandedType(session) ?: return null
    return FirCallableSignature(session) {
        val valueParameters = when (this@getCallableSignature) {
            is FirFunctionSymbol<*> -> valueParameterSymbols.map {
                it.name to it.resolvedReturnType.asRawType()
            }
            else -> emptyList()
        }
        CallableSignature(
            rawStatus.isSuspend,
            valueParameters,
            createType(returnType)
        )
    }
}

private fun FirCallableSignature.Builder.createType(rawType: ConeKotlinType): CallableSignature.Type {
    if (rawType !is ConeClassLikeType) return rawType.asRawType()
    val types = sequence {
        yield(rawType)
        val symbol = rawType.lookupTag.toSymbol(session) ?: return@sequence
        yieldAll(lookupSuperTypes(symbol, lookupInterfaces = true, deep = true, session))
    }
    for (type in types) {
        when (val classId = type.lookupTag.classId) {
            ClassIds.stateFlow, ClassIds.mutableStateFlow -> {
                val valueType = type.flowValueType.asRawType()
                val isMutable = classId == ClassIds.mutableStateFlow && !rawType.isMarkedNullable
                return rawType.asStateFlow(valueType, isMutable)
            }
            ClassIds.sharedFlow -> {
                val valueType = type.flowValueType.asRawType()
                return rawType.asSharedFlow(valueType)
            }
            ClassIds.flow -> {
                val valueType = type.flowValueType.asRawType()
                return rawType.asSimpleFlow(valueType)
            }
        }
    }
    return rawType.asRawType()
}

private val ConeKotlinType.flowValueType: ConeKotlinType
    get() = when (val argument = typeArguments.firstOrNull()) {
        is ConeKotlinTypeProjection -> argument.type
        is ConeStarProjection, null -> StandardClassIds.Any.constructClassLikeType(isNullable = true)
    }
