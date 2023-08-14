package com.rickclephas.kmp.nativecoroutines.compiler.config

import org.jetbrains.kotlin.config.CompilerConfiguration

internal operator fun <T: Any> CompilerConfiguration.set(option: ConfigOption<T>, value: String) =
    put(option.configKey, option.parse(value))

internal operator fun <T: Any> CompilerConfiguration.get(option: ConfigOption<T>): T? =
    get(option.configKey)

internal operator fun <T: Any> CompilerConfiguration.get(option: ConfigOptionWithDefault<T>): T =
    get(option as ConfigOption<T>) ?: option.defaultValue
