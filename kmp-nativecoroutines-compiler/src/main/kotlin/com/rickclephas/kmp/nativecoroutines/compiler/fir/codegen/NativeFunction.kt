package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.*
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import com.rickclephas.kmp.nativecoroutines.compiler.utils.shouldRefineInSwift
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.CallableId

internal fun FirExtension.buildNativeFunction(
    callableId: CallableId,
    originalSymbol: FirNamedFunctionSymbol,
    annotation: NativeCoroutinesAnnotation
): FirNamedFunctionSymbol? {
    val callableSignature = originalSymbol.getCallableSignature(session) ?: return null
    return buildSimpleFunction {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        origin = NativeCoroutinesDeclarationKey(
            annotation,
            callableSignature
        ).origin

        source = originalSymbol.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirNamedFunctionSymbol(callableId)
        name = callableId.callableName

        status = FirResolvedDeclarationStatusImpl(
            Visibilities.Public,
            Modality.FINAL,
            EffectiveVisibility.Public
        )

        dispatchReceiverType = originalSymbol.dispatchReceiverType

        // TODO: support typeParameters
        if (originalSymbol.typeParameterSymbols.isNotEmpty()) return null
        // TODO: contextReceivers
        // TODO: receiverParameter

        // TODO: replace type parameters in valueParameters
        valueParameters.addAll(buildValueParametersCopy(
            originalSymbol.valueParameterSymbols,
            symbol,
            origin
        ))

        // TODO: replace type parameters in returnTypeRef
        returnTypeRef = callableSignature.returnType
            .toNativeConeKotlinType(session, originalSymbol.rawStatus.isSuspend)
            .toFirResolvedTypeRef()

        // TODO: attributes?
        // TODO: deprecationsProvider?
        // TODO: containerSource?
        // TODO: contractDescription?

        annotations.addAll(buildAnnotationsCopy(
            originalSymbol.annotations,
            originalSymbol.name.identifier,
            setOf(
                NativeCoroutinesAnnotation.NativeCoroutines.classId,
                NativeCoroutinesAnnotation.NativeCoroutinesRefined.classId,
                ClassIds.throws,
            )
        ))
        if (annotation.shouldRefineInSwift) {
            annotations.add(buildAnnotation(ClassIds.shouldRefineInSwift))
        }
    }.symbol
}