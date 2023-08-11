package com.rickclephas.kmp.nativecoroutines.compiler.config

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey

abstract class ConfigOption<T: Any>(final override val optionName: String): AbstractCliOption {
    override val required: Boolean = true
    override val allowMultipleOccurrences: Boolean = false
    val configKey = CompilerConfigurationKey<T>(optionName)
    abstract fun parse(value: String): T
}

abstract class ConfigOptionWithDefault<T: Any>(optionName: String): ConfigOption<T>(optionName) {
    abstract val defaultValue: T
}
