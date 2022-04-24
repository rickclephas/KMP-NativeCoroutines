package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

internal class KmpNativeCoroutinesSymbolProcessor(
    private val logger: KSPLogger
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("KmpNativeCoroutinesSymbolProcessor")
        return emptyList()
    }
}