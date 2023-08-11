package com.rickclephas.kmp.nativecoroutines.compiler.config

import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesOptionNames as OptionNames
import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal object KmpNativeCoroutinesConfigurationKeys {
    val EXPOSED_SEVERITY = CompilerConfigurationKey<ExposedSeverity>(OptionNames.EXPOSED_SEVERITY)
}
