package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef

internal fun FirExtension.buildValueParametersCopy(
    originalParameters: List<FirValueParameterSymbol>,
    containingFunctionSymbol: FirFunctionSymbol<*>,
    origin: FirDeclarationOrigin,
): List<FirValueParameter> = originalParameters.map { parameter ->
    buildValueParameter {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        this.origin = origin

        source = parameter.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirValueParameterSymbol(parameter.name)
        name = parameter.name
        this.containingFunctionSymbol = containingFunctionSymbol

        isCrossinline = parameter.isCrossinline
        isNoinline = parameter.isNoinline
        isVararg = parameter.isVararg

        // TODO: dispatchReceiverType
        // TODO: contextReceivers

        // TODO: can we access resolvedReturnTypeRef?
        returnTypeRef = parameter.resolvedReturnTypeRef.let {
            it.coneType.toFirResolvedTypeRef(
                it.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
            )
        }

        // TODO: attributes?
        // TODO: deprecationProvider?
        // TODO: containerSource?
        // TODO: backingField?
        // TODO: defaultValue?

        annotations.addAll(buildAnnotationsCopy(parameter.annotations))
    }
}
