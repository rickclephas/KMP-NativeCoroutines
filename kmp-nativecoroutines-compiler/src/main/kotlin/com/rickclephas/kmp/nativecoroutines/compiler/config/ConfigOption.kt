package com.rickclephas.kmp.nativecoroutines.compiler.config

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey

public abstract class ConfigOption<T: Any>(final override val optionName: String): AbstractCliOption {
    override val required: Boolean = true
    override val allowMultipleOccurrences: Boolean = false
    public val configKey: CompilerConfigurationKey<T> = CompilerConfigurationKey<T>(optionName)
    public abstract fun parse(value: String): T
}

public abstract class ConfigOptionWithDefault<T: Any>(optionName: String): ConfigOption<T>(optionName) {
    public abstract val defaultValue: T
}
