package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.*
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import com.rickclephas.kmp.nativecoroutines.compiler.utils.shouldRefineInSwift
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.origin
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.CallableId

internal fun FirExtension.buildNativeFunction(
    callableId: CallableId,
    originalSymbol: FirNamedFunctionSymbol,
    annotation: NativeCoroutinesAnnotation
): FirNamedFunctionSymbol? {
    val firCallableSignature = originalSymbol.getCallableSignature(session) ?: return null
    val callableSignature = firCallableSignature.signature
    return buildSimpleFunction {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        moduleData = session.moduleData
        origin = NativeCoroutinesDeclarationKey(
            NativeCoroutinesDeclarationKey.Type.NATIVE,
            callableSignature
        ).origin

        source = originalSymbol.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)

        symbol = FirNamedFunctionSymbol(callableId)
        name = callableId.callableName

        status = originalSymbol.getGeneratedDeclarationStatus(session) ?: return null

        dispatchReceiverType = originalSymbol.dispatchReceiverType

        val originalTypeParameters = originalSymbol.typeParameterSymbols
        val typeParameters = buildTypeParametersCopy(
            originalTypeParameters,
            symbol,
            origin
        )
        this.typeParameters.addAll(typeParameters.parameters)

        // TODO: support contextReceivers once exported to ObjC

        receiverParameter = buildReceiverParameterCopy(
            originalSymbol.receiverParameter,
            typeParameters.substitutor
        )
        valueParameters.addAll(buildValueParametersCopy(
            originalSymbol.valueParameterSymbols,
            symbol,
            origin,
            typeParameters.substitutor
        ))

        returnTypeRef = firCallableSignature.getNativeType(
            callableSignature.returnType,
            callableSignature.isSuspend
        ).let(typeParameters.substitutor::substituteOrSelf).toFirResolvedTypeRef()

        @OptIn(SymbolInternals::class)
        deprecationsProvider = originalSymbol.fir.deprecationsProvider

        annotations.addAll(buildAnnotationsCopy(originalSymbol.annotations, originalSymbol.name.identifier))
        if (annotation.shouldRefineInSwift) {
            annotations.add(buildAnnotation(ClassIds.shouldRefineInSwift))
        }

        body = session.buildCallableReferenceBlock(originalSymbol)
    }.symbol
}
