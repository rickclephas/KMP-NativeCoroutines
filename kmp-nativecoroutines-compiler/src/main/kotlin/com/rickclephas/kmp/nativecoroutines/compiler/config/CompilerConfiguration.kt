package com.rickclephas.kmp.nativecoroutines.compiler.config

import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesConfigurationKeys as ConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration

internal val CompilerConfiguration.exposedSeverity: ExposedSeverity
    get() = get(ConfigurationKeys.EXPOSED_SEVERITY, ExposedSeverity.WARNING)
