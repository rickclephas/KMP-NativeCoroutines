package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.FirCallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.getNativeType
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirReceiverParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef

internal fun FirExtension.buildReceiverParameterCopy(
    originalParameter: FirReceiverParameter?,
    firCallableSignature: FirCallableSignature,
    substitutor: ConeSubstitutor
): FirReceiverParameter? {
    if (originalParameter == null) return null
    return buildReceiverParameter {
        source = originalParameter.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        val receiverType = requireNotNull(firCallableSignature.signature.receiverType)
        typeRef = firCallableSignature.getNativeType(receiverType)
            .let(substitutor::substituteOrSelf)
            .toFirResolvedTypeRef()

        annotations.addAll(buildAnnotationsCopy(originalParameter.annotations))
    }
}
