package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.Name

internal class NameGenerator(private val suffix: String) {

    fun createNativeName(name: Name): Name =
        Name.identifier("${name.identifier}$suffix")

    fun isNativeName(name: Name): Boolean =
        !name.isSpecial && name.identifier.endsWith(suffix)

    fun createOriginalName(nativeName: Name): Name =
        Name.identifier(nativeName.identifier.removeSuffix(suffix))
}