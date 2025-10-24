package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.Name

internal fun Name.withSuffix(suffix: String?): Name? {
    if (suffix == null) return null
    return Name.identifier("$identifier$suffix")
}

internal fun Name.withoutSuffix(suffix: String?): Name? {
    if (suffix == null) return null
    val identifier = identifier
    if (!identifier.endsWith(suffix)) return null
    return Name.identifier(identifier.dropLast(suffix.length))
}
