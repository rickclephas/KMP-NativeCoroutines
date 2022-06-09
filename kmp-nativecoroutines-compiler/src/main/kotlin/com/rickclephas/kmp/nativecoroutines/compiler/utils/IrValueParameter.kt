package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.classifierOrNull

internal fun IrValueParameter.isSameAs(other: IrValueParameter): Boolean =
    index == other.index && name == other.name && isCrossinline == other.isCrossinline &&
            isNoinline == other.isNoinline && isHidden == other.isHidden && hasSameTypeAs(other)

internal fun List<IrValueParameter>.areSameAs(others: List<IrValueParameter>): Boolean {
    if (size != others.size) return false
    for (i in indices) {
        if (!this[i].isSameAs(others[i])) return false
    }
    return true
}

private fun IrValueParameter.hasSameTypeAs(other: IrValueParameter): Boolean {
    val paramParent = parent
    val otherParamParent = other.parent
    fun IrType.isSameTypeAs(other: IrType): Boolean {
        // Check the types if this isn't a generic type or if the generic type isn't on the function
        val classifier = classifierOrNull
        val typeParameter = classifier?.owner as? IrTypeParameter
        if (classifier !is IrTypeParameterSymbol || typeParameter?.parent != paramParent) {
            // If these aren't simple types just use the equals check
            if (this !is IrSimpleType || other !is IrSimpleType)
                return this == other
            // else run the same code from the equals check except for the arguments
            // https://github.com/JetBrains/kotlin/blob/01a24fdc0821a5c598a9bbd54774198730c044bd/compiler/ir/ir.tree/src/org/jetbrains/kotlin/ir/types/impl/IrSimpleTypeImpl.kt#L27
            if (!FqNameEqualityChecker.areEqual(this.classifier, other.classifier) ||
                nullability != other.nullability || arguments.size != other.arguments.size)
                return false
            // Check if the arguments are the same
            for (i in arguments.indices) {
                val argument = arguments[i]
                val otherArgument = other.arguments[i]
                // For type projections we need to check the type manually
                // else we'll just use the equals check
                if (argument is IrTypeProjection && otherArgument is IrTypeProjection) {
                    // Run the same code from the equals check except for the type
                    // https://github.com/JetBrains/kotlin/blob/486c6b3c15dfa245693c3df2c58c8353d75deddb/compiler/ir/ir.tree/src/org/jetbrains/kotlin/ir/types/impl/IrSimpleTypeImpl.kt#L116
                    if (argument.variance != otherArgument.variance ||
                        !argument.type.isSameTypeAs(otherArgument.type)
                    ) return false
                } else {
                    if (argument != otherArgument) return false
                }
            }
            return true
        }
        // else make sure the other type is also a generic type on the function
        val otherTypeParameter = other.classifierOrNull?.owner as? IrTypeParameter
        if (otherTypeParameter?.parent != otherParamParent) return false
        // Check if the generic types are equal
        if (typeParameter.name != otherTypeParameter.name ||
            typeParameter.index != otherTypeParameter.index ||
            typeParameter.isReified != otherTypeParameter.isReified ||
            typeParameter.variance != otherTypeParameter.variance
        ) return false
        // Check if the super types are equal
        val superTypes = typeParameter.superTypes
        val otherSuperTypes = otherTypeParameter.superTypes
        if (superTypes.size != otherSuperTypes.size) return false
        for (i in superTypes.indices)
            if (!superTypes[i].isSameTypeAs(otherSuperTypes[i])) return false
        return true
    }
    return type.isSameTypeAs(other.type) && run {
        val varargElementType = varargElementType
        val otherVarargElementType = other.varargElementType
        if (varargElementType != null && otherVarargElementType != null)
            return@run varargElementType.isSameTypeAs(otherVarargElementType)
        return@run varargElementType == null && otherVarargElementType == null
    }
}