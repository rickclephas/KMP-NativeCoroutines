package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef

internal fun FirExtension.buildValueParametersCopy(
    originalParameters: List<FirValueParameterSymbol>,
    containingDeclarationSymbol: FirFunctionSymbol<*>,
    origin: FirDeclarationOrigin,
    substitutor: ConeSubstitutor,
): List<FirValueParameter> = originalParameters.map { parameter ->
    buildValueParameter {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        this.origin = origin

        source = parameter.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirValueParameterSymbol(parameter.name)
        name = parameter.name
        this.containingDeclarationSymbol = containingDeclarationSymbol

        isCrossinline = parameter.isCrossinline
        isNoinline = parameter.isNoinline
        isVararg = parameter.isVararg

        // TODO: support contextReceivers once exported to ObjC

        returnTypeRef = parameter.resolvedReturnTypeRef.coneType
            .let(substitutor::substituteOrSelf)
            .toFirResolvedTypeRef()

        @OptIn(SymbolInternals::class)
        deprecationsProvider = parameter.fir.deprecationsProvider

        // TODO: support defaultValue once exported to ObjC

        annotations.addAll(buildAnnotationsCopy(parameter.annotations))
    }
}
