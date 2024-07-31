package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.getClassDeclaredFunctionSymbols
import org.jetbrains.kotlin.fir.resolve.providers.getClassDeclaredPropertySymbols
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.CallableId

/**
 * Returns the top-level or class declared function symbols for the provided [callableId].
 * @see FirSymbolProvider.getTopLevelFunctionSymbols
 * @see FirSymbolProvider.getClassDeclaredFunctionSymbols
 */
internal fun FirSymbolProvider.getFunctionSymbols(
    callableId: CallableId
): List<FirNamedFunctionSymbol> = when (val classId = callableId.classId) {
    null -> getTopLevelFunctionSymbols(callableId.packageName, callableId.callableName)
    else -> getClassDeclaredFunctionSymbols(classId, callableId.callableName)
}

/**
 * Returns the top-level or class declared property symbols for the provided [callableId].
 * @see FirSymbolProvider.getTopLevelPropertySymbols
 * @see FirSymbolProvider.getClassDeclaredPropertySymbols
 */
internal fun FirSymbolProvider.getPropertySymbols(
    callableId: CallableId
): List<FirPropertySymbol> = when (val classId = callableId.classId) {
    null -> getTopLevelPropertySymbols(callableId.packageName, callableId.callableName)
    else -> getClassDeclaredPropertySymbols(classId, callableId.callableName).filterIsInstance<FirPropertySymbol>()
}
