package com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesDeclarationChecker
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

internal class KmpNativeCoroutinesFirAdditionalCheckersExtension(
    session: FirSession,
    exposedSeverity: ExposedSeverity
): FirAdditionalCheckersExtension(session) {

    internal class Factory(
        private val exposedSeverity: ExposedSeverity
    ): FirAdditionalCheckersExtension.Factory {
        override fun create(session: FirSession): KmpNativeCoroutinesFirAdditionalCheckersExtension =
            KmpNativeCoroutinesFirAdditionalCheckersExtension(session, exposedSeverity)
    }

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val callableDeclarationCheckers = setOf(FirKmpNativeCoroutinesDeclarationChecker(exposedSeverity))
    }
}
