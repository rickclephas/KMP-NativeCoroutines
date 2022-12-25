package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal enum class ExposedSeverity {
    NONE, WARNING, ERROR
}

internal const val EXPOSED_SEVERITY_OPTION_NAME = "exposedSeverity"
internal val EXPOSED_SEVERITY_KEY = CompilerConfigurationKey<ExposedSeverity>(EXPOSED_SEVERITY_OPTION_NAME)
