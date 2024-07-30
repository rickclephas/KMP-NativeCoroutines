package com.rickclephas.kmp.nativecoroutines.compiler.config

public val K2_MODE: ConfigOptionWithDefault<Boolean> =
    object : ConfigOptionWithDefault<Boolean>("k2Mode") {
        override val required: Boolean = false
        override val description: String = "Indicates if the plugin should be run in K2 mode"
        override val valueDescription: String = "true/false"
        override val defaultValue: Boolean = false
        override fun parse(value: String): Boolean = value.toBooleanStrict()
    }
