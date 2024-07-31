package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassLookupTag
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.toEffectiveVisibility

internal fun FirCallableSymbol<*>.getGeneratedDeclarationStatus(session: FirSession): FirDeclarationStatus? {
    val visibility = rawStatus.visibility
    if (!visibility.isPublicAPI) return null
    return FirResolvedDeclarationStatusImpl(
        visibility,
        Modality.FINAL,
        visibility.toEffectiveVisibility(containingClassLookupTag()?.toSymbol(session))
    )
}
