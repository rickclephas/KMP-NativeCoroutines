package com.rickclephas.kmp.nativecoroutines.compiler.config

public enum class ExposedSeverity {
    NONE, WARNING, ERROR
}

public val EXPOSED_SEVERITY: ConfigOptionWithDefault<ExposedSeverity> =
    object : ConfigOptionWithDefault<ExposedSeverity>("exposedSeverity") {
        override val description: String = "Specifies the severity of the exposed coroutines check"
        override val valueDescription: String = "NONE/WARNING/ERROR"
        override val defaultValue: ExposedSeverity = ExposedSeverity.WARNING
        override fun parse(value: String): ExposedSeverity = ExposedSeverity.valueOf(value)
    }
