package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.Name

internal class NameGenerator(private val suffix: String) {

    fun createNativeName(name: Name): Name =
        Name.identifier("${name.identifier}$suffix")

    fun isNativeName(name: Name): Boolean =
        !name.isSpecial && name.identifier.endsWith(suffix)

    fun createNativeValueName(name: Name): Name =
        Name.identifier("${name.identifier}${suffix}Value")

    fun isNativeValueName(name: Name): Boolean =
        !name.isSpecial && name.identifier.endsWith("${suffix}Value")

    fun createNativeReplayCacheName(name: Name): Name =
        Name.identifier("${name.identifier}${suffix}ReplayCache")

    fun isNativeReplayCacheName(name: Name): Boolean =
        !name.isSpecial && name.identifier.endsWith("${suffix}ReplayCache")

    private val regex = Regex("${suffix}(Value|ReplayCache)?$")

    fun createOriginalName(nativeName: Name): Name =
        Name.identifier(nativeName.identifier.replace(regex, ""))
}