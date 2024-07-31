package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirUserTypeRef

/**
 * Safely returns the [resolvedReturnTypeRef][FirCallableSymbol.resolvedReturnTypeRef].
 * Note this returns `null` if the reference can't be resolved.
 */
@OptIn(SymbolInternals::class)
internal val FirCallableSymbol<*>.resolvedReturnTypeRefOrNull: FirResolvedTypeRef?
    get() = when (fir.returnTypeRef) {
        is FirResolvedTypeRef, is FirUserTypeRef -> resolvedReturnTypeRef
        else -> null
    }
