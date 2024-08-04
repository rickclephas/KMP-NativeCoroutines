package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.orRaw
import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.resolve.*
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

        fun ConeKotlinType.asRawType(isInput: Boolean): CallableSignature.Type.Raw =
            CallableSignature.Type.Raw(rawTypeIndex, isMarkedNullable, isInput)

        fun ConeKotlinType.asSimpleFlow(
            isInput: Boolean,
            valueType: CallableSignature.Type,
            isCustom: Boolean
        ): CallableSignature.Type.Flow.Simple =
            CallableSignature.Type.Flow.Simple(rawTypeIndex, isMarkedNullable, isInput, valueType, isCustom)

        fun ConeKotlinType.asSharedFlow(
            isInput: Boolean,
            valueType: CallableSignature.Type
        ): CallableSignature.Type.Flow.Shared =
            CallableSignature.Type.Flow.Shared(rawTypeIndex, isMarkedNullable, isInput, valueType)

        fun ConeKotlinType.asStateFlow(
            isInput: Boolean,
            valueType: CallableSignature.Type,
            isMutable: Boolean
        ): CallableSignature.Type.Flow.State =
            CallableSignature.Type.Flow.State(rawTypeIndex, isMarkedNullable, isInput, valueType, isMutable)

        fun ConeKotlinType.asFunction(
            isInput: Boolean,
            isSuspend: Boolean,
            receiverType: CallableSignature.Type?,
            valueTypes: List<CallableSignature.Type>,
            returnType: CallableSignature.Type
        ): CallableSignature.Type.Function =
            CallableSignature.Type.Function(rawTypeIndex, isMarkedNullable, isInput, isSuspend, receiverType, valueTypes, returnType)

        fun build(signature: CallableSignature): FirCallableSignature =
            FirCallableSignature(signature, rawTypes)
    }

    companion object {
        operator fun invoke(session: FirSession, block: Builder.() -> CallableSignature): FirCallableSignature =
            Builder(session).run { build(block()) }
    }
}

internal fun FirCallableSymbol<*>.getCallableSignature(session: FirSession): FirCallableSignature? {
    return getCallableSignature(session, resolvedReturnTypeRefOrNull?.type ?: return null)
}

internal fun FirCallableDeclaration.getCallableSignature(session: FirSession): FirCallableSignature {
    return symbol.getCallableSignature(session, symbol.resolvedReturnTypeRef.type)
}

private fun FirCallableSymbol<*>.getCallableSignature(
    session: FirSession,
    returnType: ConeKotlinType
): FirCallableSignature = FirCallableSignature(session) {
    val valueTypes = when (this@getCallableSignature) {
        is FirFunctionSymbol<*> -> valueParameterSymbols.map {
            createType(it.resolvedReturnType, isInput = true)
        }
        else -> emptyList()
    }
    CallableSignature(
        rawStatus.isSuspend,
        resolvedReceiverTypeRef?.type?.let { createType(it, isInput = true) },
        valueTypes,
        createType(returnType, isInput = false)
    )
}

private fun FirCallableSignature.Builder.createType(
    rawType: ConeKotlinType,
    isInput: Boolean
): CallableSignature.Type {
    val expandedType = rawType.fullyExpandedType(session)
    createFunctionType(expandedType, isInput)?.let { return it }
    if (expandedType !is ConeClassLikeType) return expandedType.asRawType(isInput)
    val types = sequence {
        yield(expandedType)
        val symbol = expandedType.lookupTag.toClassSymbol(session) ?: return@sequence
        val substitutor = createSubstitutionForSupertype(expandedType, session)
        val superTypes = lookupSuperTypes(listOf(symbol), lookupInterfaces = true, deep = true, session, substituteTypes = true)
        yieldAll(superTypes.map { substitutor.substituteOrSelf(it) as ConeClassLikeType })
    }
    types.forEachIndexed { index, type ->
        when (val classId = type.lookupTag.classId) {
            ClassIds.stateFlow, ClassIds.mutableStateFlow -> {
                val valueType = type.flowValueType.asRawType(isInput)
                val isMutable = classId == ClassIds.mutableStateFlow && !expandedType.isMarkedNullable
                return expandedType.asStateFlow(isInput, valueType, isMutable)
            }
            ClassIds.sharedFlow -> {
                val valueType = type.flowValueType.asRawType(isInput)
                return expandedType.asSharedFlow(isInput, valueType)
            }
            ClassIds.flow -> {
                val valueType = type.flowValueType.asRawType(isInput)
                return expandedType.asSimpleFlow(isInput, valueType, isCustom = index != 0)
            }
        }
    }
    return expandedType.asRawType(isInput)
}

private fun FirCallableSignature.Builder.createFunctionType(
    rawType: ConeKotlinType,
    isInput: Boolean
): CallableSignature.Type? {
    val functionType = rawType.functionTypeKind(session) ?: return null
    val isFunctionType = functionType == FunctionTypeKind.Function
    val isSuspendFunctionType = functionType == FunctionTypeKind.SuspendFunction
    if (!isFunctionType && !isSuspendFunctionType) return null
    return rawType.asFunction(
        isInput,
        isSuspendFunctionType,
        rawType.receiverType(session)?.let { createType(it, !isInput) },
        rawType.valueParameterTypesWithoutReceivers(session).map { createType(it, !isInput) },
        createType(rawType.returnType(session), isInput)
    ).orRaw()
}

private val ConeKotlinType.flowValueType: ConeKotlinType
    get() = when (val argument = typeArguments.firstOrNull()) {
        is ConeKotlinTypeProjection -> argument.type
        is ConeStarProjection, null -> StandardClassIds.Any.constructClassLikeType(isNullable = true)
    }
