package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.FirPropertyBuilder
import org.jetbrains.kotlin.fir.declarations.builder.buildDefaultSetterValueParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildPropertyAccessor
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertyAccessorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.impl.FirImplicitUnitTypeRef

internal fun FirExtension.buildPropertyGetter(
    propertyBuilder: FirPropertyBuilder,
    originalSymbol: FirPropertySymbol,
): FirPropertyAccessor = buildPropertyAccessor {
    val originalGetter = originalSymbol.getterSymbol
    require(originalGetter != null)
    resolvePhase = FirResolvePhase.BODY_RESOLVE
    moduleData = propertyBuilder.moduleData
    origin = propertyBuilder.origin
    status = propertyBuilder.status
    returnTypeRef = propertyBuilder.returnTypeRef
    symbol = FirPropertyAccessorSymbol()
    propertySymbol = propertyBuilder.symbol
    isGetter = true
    annotations.addAll(buildAnnotationsCopy(originalGetter.resolvedAnnotationsWithClassIds))
    body = session.buildCallableReferenceBlock(originalSymbol)
}

internal fun FirExtension.buildPropertySetter(
    propertyBuilder: FirPropertyBuilder,
    originalSymbol: FirPropertySymbol,
): FirPropertyAccessor = buildPropertyAccessor {
    val originalGetter = originalSymbol.getterSymbol
    require(originalGetter != null)
    resolvePhase = FirResolvePhase.BODY_RESOLVE
    moduleData = propertyBuilder.moduleData
    origin = propertyBuilder.origin
    status = propertyBuilder.status
    returnTypeRef = FirImplicitUnitTypeRef(null)
    symbol = FirPropertyAccessorSymbol()
    propertySymbol = propertyBuilder.symbol
    valueParameters.add(
        buildDefaultSetterValueParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            containingDeclarationSymbol = this@buildPropertyAccessor.symbol
            moduleData = propertyBuilder.moduleData
            origin = propertyBuilder.origin
            returnTypeRef = propertyBuilder.returnTypeRef
            symbol = FirValueParameterSymbol(StandardNames.DEFAULT_VALUE_PARAMETER)
        }
    )
    isGetter = false
    annotations.addAll(buildAnnotationsCopy(originalGetter.resolvedAnnotationsWithClassIds))
}
