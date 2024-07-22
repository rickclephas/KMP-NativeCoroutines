package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.FirPropertyBuilder
import org.jetbrains.kotlin.fir.declarations.builder.buildDefaultSetterValueParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildPropertyAccessor
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertyAccessorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.impl.FirImplicitUnitTypeRef

internal fun FirPropertyBuilder.buildPropertyGetter(
    session: FirSession,
    originalSymbol: FirPropertySymbol,
): FirPropertyAccessor = buildPropertyAccessor {
    resolvePhase = FirResolvePhase.BODY_RESOLVE
    moduleData = this@buildPropertyGetter.moduleData
    origin = this@buildPropertyGetter.origin
    status = this@buildPropertyGetter.status
    returnTypeRef = this@buildPropertyGetter.returnTypeRef
    symbol = FirPropertyAccessorSymbol()
    propertySymbol = this@buildPropertyGetter.symbol
    isGetter = true
    body = session.buildCallableReferenceBlock(originalSymbol)
}

internal fun FirPropertyBuilder.buildPropertySetter(): FirPropertyAccessor = buildPropertyAccessor {
    resolvePhase = FirResolvePhase.BODY_RESOLVE
    moduleData = this@buildPropertySetter.moduleData
    origin = this@buildPropertySetter.origin
    status = this@buildPropertySetter.status
    returnTypeRef = FirImplicitUnitTypeRef(null)
    symbol = FirPropertyAccessorSymbol()
    propertySymbol = this@buildPropertySetter.symbol
    valueParameters.add(
        buildDefaultSetterValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            containingFunctionSymbol = this@buildPropertyAccessor.symbol
            moduleData = this@buildPropertySetter.moduleData
            origin = this@buildPropertySetter.origin
            returnTypeRef = this@buildPropertySetter.returnTypeRef
            symbol = FirValueParameterSymbol(StandardNames.DEFAULT_VALUE_PARAMETER)
        }
    )
    isGetter = false
}
