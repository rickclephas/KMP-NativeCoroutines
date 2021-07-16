package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.declarations.IrValueParameter

internal fun IrValueParameter.isSameAs(other: IrValueParameter): Boolean =
    index == other.index && name == other.name && type == other.type && varargElementType == other.varargElementType
            && isCrossinline == other.isCrossinline && isNoinline == other.isNoinline && isHidden == other.isHidden

internal fun List<IrValueParameter>.areSameAs(others: List<IrValueParameter>): Boolean {
    if (size != others.size) return false
    for (i in indices) {
        if (!this[i].isSameAs(others[i])) return false
    }
    return true
}
