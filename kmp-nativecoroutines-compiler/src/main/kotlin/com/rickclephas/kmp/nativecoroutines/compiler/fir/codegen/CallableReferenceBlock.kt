package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.builder.buildBlock
import org.jetbrains.kotlin.fir.expressions.builder.buildCallableReferenceAccess
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.utils.addIfNotNull

internal fun FirSession.buildCallableReferenceBlock(
    symbol: FirCallableSymbol<*>
): FirBlock = buildBlock {
    coneTypeOrNull = StandardClassIds.Nothing.constructClassLikeType()
    statements += buildCallableReferenceAccess {
        coneTypeOrNull = symbol.getReferenceConeType()
        calleeReference = buildResolvedNamedReference {
            name = symbol.name
            resolvedSymbol = symbol
        }
    }
    statements += buildTodoCall("KMP-NativeCoroutines generated declaration must be implemented in IR")
}

private fun FirCallableSymbol<*>.getReferenceConeType(): ConeKotlinType = when (this) {
    is FirNamedFunctionSymbol -> getReferenceConeType()
    is FirPropertySymbol -> getReferenceConeType()
    else -> throw IllegalArgumentException("Unsupported callable symbol $this")
}

private fun FirNamedFunctionSymbol.getReferenceConeType(): ConeKotlinType {
    val typeArguments = buildList {
        addIfNotNull(receiverParameter?.typeRef?.coneType)
        addAll(valueParameterSymbols.map { it.resolvedReturnType })
        add(resolvedReturnType)
    }
    val functionTypeKind = if (isSuspend) FunctionTypeKind.KSuspendFunction else FunctionTypeKind.KFunction
    val arity = typeArguments.size - 1
    return functionTypeKind.numberedClassId(arity).constructClassLikeType(typeArguments.toTypedArray())
}

private fun FirPropertySymbol.getReferenceConeType(): ConeKotlinType {
    val isVar = isVar
    val isMember = dispatchReceiverType != null
    val isExtension = receiverParameter != null
    val classId = when {
        isMember && isExtension -> throw UnsupportedOperationException("Member-extension properties aren't supported")
        isMember || isExtension -> if (isVar) StandardClassIds.KMutableProperty1 else StandardClassIds.KProperty1
        else -> if (isVar) StandardClassIds.KMutableProperty0 else StandardClassIds.KProperty0
    }
    val typeArguments = buildList {
        addIfNotNull(dispatchReceiverType)
        addIfNotNull(receiverParameter?.typeRef?.coneType)
    }
    return classId.constructClassLikeType(typeArguments.toTypedArray())
}
