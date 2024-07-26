package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.*
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesState
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.compiler.utils.shouldRefineInSwift
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirPropertyBodyResolveState
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.CallableId

internal fun FirExtension.buildStateFlowValueProperty(
    callableId: CallableId,
    originalSymbol: FirPropertySymbol,
    annotation: NativeCoroutinesAnnotation
): FirPropertySymbol? {
    val firCallableSignature = originalSymbol.getCallableSignature(session) ?: return null
    val callableSignature = firCallableSignature.signature
    if (callableSignature.isSuspend) return null
    if (callableSignature.returnType !is CallableSignature.Type.Flow.State) return null
    return buildProperty {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        origin = NativeCoroutinesDeclarationKey(
            NativeCoroutinesDeclarationKey.Type.STATE_FLOW_VALUE,
            callableSignature
        ).origin

        source = originalSymbol.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirPropertySymbol(callableId)
        name = callableId.callableName

        status = FirResolvedDeclarationStatusImpl(
            Visibilities.Public, // TODO: support protected visibility
            Modality.FINAL,
            EffectiveVisibility.Public
        )

        dispatchReceiverType = originalSymbol.dispatchReceiverType

        val typeParameters = buildTypeParametersCopy(
            originalSymbol.typeParameterSymbols,
            symbol,
            origin
        )
        this.typeParameters.addAll(typeParameters.parameters)

        // TODO: contextReceivers

        receiverParameter = buildReceiverParameterCopy(
            originalSymbol.receiverParameter,
            typeParameters.substitutor
        )

        returnTypeRef = firCallableSignature.getNativeType(callableSignature.returnType.valueType)
            .let(typeParameters.substitutor::substituteOrSelf)
            .toFirResolvedTypeRef()

        isVar = callableSignature.returnType.isMutable
        getter = buildPropertyGetter(session, originalSymbol)
        if (isVar) {
            setter = buildPropertySetter()
        }

        isLocal = false
        bodyResolveState = FirPropertyBodyResolveState.ALL_BODIES_RESOLVED

        @OptIn(SymbolInternals::class)
        deprecationsProvider = originalSymbol.fir.deprecationsProvider

        val objCName = when (annotation) {
            NativeCoroutinesState,
            NativeCoroutinesRefinedState -> originalSymbol.name.identifier
            else -> null
        }
        annotations.addAll(buildAnnotationsCopy(
            originalSymbol.annotations,
            objCName,
            setOf(
                NativeCoroutines.classId,
                NativeCoroutinesRefined.classId,
                NativeCoroutinesState.classId,
                NativeCoroutinesRefinedState.classId,
                ClassIds.throws,
            )
        ))
        if (annotation.shouldRefineInSwift) {
            annotations.add(buildAnnotation(ClassIds.shouldRefineInSwift))
        }
    }.symbol
}
