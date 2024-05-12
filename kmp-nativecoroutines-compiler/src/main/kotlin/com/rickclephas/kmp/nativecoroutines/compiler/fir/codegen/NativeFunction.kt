package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.*
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.resolvedReturnTypeRefOrNull
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.CallableId

private fun FirExtension.buildNativeFunction(
    callableId: CallableId,
    owner: FirClassSymbol<*>?,
    originalSymbol: FirNamedFunctionSymbol,
    isRefined: Boolean
): FirNamedFunctionSymbol? {
    val originalReturnTypeRef = originalSymbol.resolvedReturnTypeRefOrNull ?: return null
    return buildSimpleFunction {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        // TODO: set origin

        source = originalSymbol.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirNamedFunctionSymbol(callableId)
        name = callableId.callableName

        status = FirResolvedDeclarationStatusImpl(
            Visibilities.Public,
            Modality.FINAL,
            EffectiveVisibility.Public
        )

        dispatchReceiverType = originalSymbol.dispatchReceiverType

        // TODO: set type parameters
        // TODO: set context receivers
        // TODO: set value parameters

        // TODO: replace type parameters in returnTypeRef
        returnTypeRef = originalReturnTypeRef.type.fullyExpandedType(session)
            .getNativeConeKotlinType(session, originalSymbol.rawStatus.isSuspend)
            .toFirResolvedTypeRef()

        // TODO: set extension type

        annotations.addAll(buildAnnotationsCopy(
            originalSymbol.annotations,
            originalSymbol.name.identifier,
            setOf(
                NativeCoroutinesAnnotation.NativeCoroutines.classId,
                NativeCoroutinesAnnotation.NativeCoroutinesRefined.classId,
                ClassIds.throws,
            )
        ))
        if (isRefined) annotations.add(buildAnnotation(ClassIds.shouldRefineInSwift))
    }.symbol
}
