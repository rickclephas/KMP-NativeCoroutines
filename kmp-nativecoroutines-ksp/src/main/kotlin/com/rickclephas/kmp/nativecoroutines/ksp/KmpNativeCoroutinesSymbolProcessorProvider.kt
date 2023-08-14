package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

public class KmpNativeCoroutinesSymbolProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val options = KmpNativeCoroutinesOptions(environment.options)
        return KmpNativeCoroutinesSymbolProcessor(environment.codeGenerator, environment.logger, options)
    }
}
