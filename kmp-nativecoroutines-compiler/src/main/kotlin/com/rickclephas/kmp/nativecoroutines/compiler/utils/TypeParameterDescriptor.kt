package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl

internal fun TypeParameterDescriptor.copyFor(
    newContainingDeclaration: DeclarationDescriptor
): TypeParameterDescriptor =
    TypeParameterDescriptorImpl.createForFurtherModification(
        newContainingDeclaration,
        annotations,
        isReified,
        variance,
        name,
        index,
        SourceElement.NO_SOURCE,
        storageManager
    ).apply {
        this@copyFor.upperBounds.forEach(::addUpperBound)
        setInitialized()
    }

internal fun List<TypeParameterDescriptor>.copyFor(
    newContainingDeclaration: DeclarationDescriptor
): List<TypeParameterDescriptor> =
    map { it.copyFor(newContainingDeclaration) }