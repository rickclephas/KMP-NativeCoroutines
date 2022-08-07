package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KmpNativeCoroutinesSymbolProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        // TODO: Use config values
        return KmpNativeCoroutinesSymbolProcessor(environment.codeGenerator, environment.logger, "Native")
    }
}
