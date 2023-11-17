package com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesDeclarationChecker
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

internal class KmpNativeCoroutinesFirAdditionalCheckersExtension(
    session: FirSession,
    exposedSeverity: ExposedSeverity
): FirAdditionalCheckersExtension(session) {

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val callableDeclarationCheckers = setOf(FirKmpNativeCoroutinesDeclarationChecker(exposedSeverity))
    }
}
