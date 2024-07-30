package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildTypeParameter
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.resolve.substitution.substitutorByMap
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef

internal data class TypeParameters(
    val parameters: List<FirTypeParameter>,
    val substitutor: ConeSubstitutor
)

internal fun FirExtension.buildTypeParametersCopy(
    originalParameters: List<FirTypeParameterSymbol>,
    containingSymbol: FirBasedSymbol<*>,
    origin: FirDeclarationOrigin
): TypeParameters {
    val parameters = originalParameters.map { parameter ->
        buildTypeParameter {
            resolvePhase = FirResolvePhase.BODY_RESOLVE
            moduleData = session.moduleData
            this.origin = origin

            source = parameter.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

            symbol = FirTypeParameterSymbol()
            name = parameter.name

            containingDeclarationSymbol = containingSymbol

            variance = parameter.variance
            isReified = parameter.isReified // TODO: reified shouldn't be supported

            bounds.addAll(parameter.resolvedBounds)

            annotations.addAll(buildAnnotationsCopy(parameter.annotations))
        }
    }

    val substitutionMap = originalParameters.zip(parameters) { from, to -> from to to.toConeType() }.toMap()
    val substitutor = substitutorByMap(substitutionMap, session)
    for (parameter in parameters) {
        val bounds = parameter.bounds.map {
            // TODO: copy type ref annotations?
            it.coneType.let(substitutor::substituteOrSelf).toFirResolvedTypeRef()
        }
        parameter.replaceBounds(bounds)
    }

    return TypeParameters(parameters, substitutor)
}
