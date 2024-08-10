package com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.GENERATED_SOURCE_DIR
import com.rickclephas.kmp.nativecoroutines.compiler.config.K2_MODE
import com.rickclephas.kmp.nativecoroutines.compiler.config.get
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesDeclarationChecker
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

internal class KmpNativeCoroutinesFirAdditionalCheckersExtension(
    session: FirSession,
    configuration: CompilerConfiguration
): FirAdditionalCheckersExtension(session) {

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val callableDeclarationCheckers = setOf(
            FirKmpNativeCoroutinesDeclarationChecker(
                exposedSeverity = configuration[EXPOSED_SEVERITY],
                generatedSourceDirs = configuration[GENERATED_SOURCE_DIR],
                isK2Mode = configuration[K2_MODE],
            )
        )
    }
}
