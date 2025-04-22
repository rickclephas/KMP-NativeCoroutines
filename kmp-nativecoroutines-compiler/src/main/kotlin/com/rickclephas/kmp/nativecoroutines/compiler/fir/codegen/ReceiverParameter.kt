package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirReceiverParameterSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef

internal fun FirExtension.buildReceiverParameterCopy(
    originalParameter: FirReceiverParameterSymbol?,
    containingDeclarationSymbol: FirBasedSymbol<*>,
    origin: FirDeclarationOrigin,
    substitutor: ConeSubstitutor
): FirReceiverParameter? {
    if (originalParameter == null) return null
    return buildReceiverParameter {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        this.origin = origin

        source = originalParameter.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirReceiverParameterSymbol()
        this.containingDeclarationSymbol = containingDeclarationSymbol

        typeRef = originalParameter.resolvedType
            .let(substitutor::substituteOrSelf)
            .toFirResolvedTypeRef()

        annotations.addAll(buildAnnotationsCopy(originalParameter.annotations))
    }
}
