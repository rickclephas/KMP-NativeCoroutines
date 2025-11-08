package com.rickclephas.kmp.nativecoroutines.compiler.config

import org.jetbrains.kotlin.utils.filterToSetOrEmpty

public enum class SwiftExport {
    NO_FUNC_RETURN_TYPES,
    NO_THROWS_SUSPEND_FUNC,
}

public val SWIFT_EXPORT: ConfigOptionWithDefault<Set<SwiftExport>> =
    object : ConfigOptionWithDefault<Set<SwiftExport>>("swiftExport") {
        override val required: Boolean = false
        override val description: String = "Specifies the Swift export compatibility version"
        override val valueDescription: String = "0"
        override val defaultValue: Set<SwiftExport> = emptySet()
        override fun parse(value: String): Set<SwiftExport> {
            val flags = value.toLong()
            return SwiftExport.entries.filterToSetOrEmpty {
                val flag = 1L shl it.ordinal
                (flags and flag) == flag
            }
        }
    }
