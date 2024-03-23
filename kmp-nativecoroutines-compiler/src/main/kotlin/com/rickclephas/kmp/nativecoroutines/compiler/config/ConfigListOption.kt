package com.rickclephas.kmp.nativecoroutines.compiler.config

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey

public abstract class ConfigListOption<T: Any>(final override val optionName: String): AbstractCliOption {
    override val required: Boolean = false
    final override val allowMultipleOccurrences: Boolean = true
    public val configKey: CompilerConfigurationKey<List<T>> = CompilerConfigurationKey<List<T>>(optionName)
    public abstract fun parse(value: String): T
}
