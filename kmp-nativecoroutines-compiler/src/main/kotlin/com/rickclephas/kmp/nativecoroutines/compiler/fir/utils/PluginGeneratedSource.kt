package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildPropertyCopy
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunctionCopy
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol

/**
 * Returns a new [FirSimpleFunction] with a [PluginGenerated][KtFakeSourceElementKind.PluginGenerated]
 * [source][FirBasedSymbol.source] for the provided [symbol].
 * Note if [symbol] doesn't have a source the original function will be returned.
 */
internal fun FirSimpleFunction.withPluginGeneratedSource(
    symbol: FirBasedSymbol<*>
): FirSimpleFunction {
    val source = symbol.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated) ?: return this
    return buildSimpleFunctionCopy(this) {
        this.source = source
    }
}

/**
 * Returns a new [FirProperty] with a [PluginGenerated][KtFakeSourceElementKind.PluginGenerated]
 * [source][FirBasedSymbol.source] for the provided [symbol].
 * Note if [symbol] doesn't have a source the original property will be returned.
 */
internal fun FirProperty.withPluginGeneratedSource(
    symbol: FirBasedSymbol<*>
): FirProperty {
    val source = symbol.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated) ?: return this
    return buildPropertyCopy(this) {
        this.source = source
    }
}
