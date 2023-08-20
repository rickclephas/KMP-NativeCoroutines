package com.rickclephas.kmp.nativecoroutines.compiler.directives

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer

internal object KmpNativeCoroutinesDirectives: SimpleDirectivesContainer() {
    val EXPOSED_SEVERITY by enumDirective<ExposedSeverity>("Specifies the severity of the exposed coroutines check")
}
