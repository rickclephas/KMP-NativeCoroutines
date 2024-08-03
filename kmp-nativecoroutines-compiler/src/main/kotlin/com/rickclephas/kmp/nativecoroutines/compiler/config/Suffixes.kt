package com.rickclephas.kmp.nativecoroutines.compiler.config

public val SUFFIX: ConfigOptionWithDefault<String> =
    object : ConfigOptionWithDefault<String>("suffix") {
        override val description: String = "Suffix used to generate the native coroutine function and property names"
        override val valueDescription: String = "String suffix"
        override val defaultValue: String = "Native"
        override fun parse(value: String): String = value
    }

public val FLOW_VALUE_SUFFIX: ConfigOption<String> =
    object : ConfigOption<String>("flowValueSuffix") {
        override val required: Boolean = false
        override val description: String = "Suffix used to generate the StateFlow value property names"
        override val valueDescription: String = "String suffix"
        override fun parse(value: String): String = value
    }

public val FLOW_REPLAY_CACHE_SUFFIX: ConfigOption<String> =
    object : ConfigOption<String>("flowReplayCacheSuffix") {
        override val required: Boolean = false
        override val description: String = "Suffix used to generate the SharedFlow replayCache property names"
        override val valueDescription: String = "String suffix"
        override fun parse(value: String): String = value
    }

public val STATE_SUFFIX: ConfigOptionWithDefault<String> =
    object : ConfigOptionWithDefault<String>("stateSuffix") {
        override val description: String = "Suffix used to generate the native state property names"
        override val valueDescription: String = "String suffix"
        override val defaultValue: String = "Value"
        override fun parse(value: String): String = value
    }

public val STATE_FLOW_SUFFIX: ConfigOption<String> =
    object : ConfigOption<String>("stateFlowSuffix") {
        override val required: Boolean = false
        override val description: String = "Suffix used to generate the StateFlow flow property names"
        override val valueDescription: String = "String suffix"
        override fun parse(value: String): String = value
    }
