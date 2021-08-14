package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl

internal fun ValueParameterDescriptor.copyFor(
    newContainingDeclaration: CallableDescriptor,
    newTypeParameters: List<TypeParameterDescriptor>
): ValueParameterDescriptor =
    ValueParameterDescriptorImpl(
        newContainingDeclaration,
        null,
        index,
        annotations,
        name,
        type.replaceFunctionGenerics(containingDeclaration, newTypeParameters),
        false, // TODO: Support and copy default values when they are exported to ObjC
        isCrossinline,
        isNoinline,
        varargElementType?.replaceFunctionGenerics(containingDeclaration, newTypeParameters),
        SourceElement.NO_SOURCE
    )

internal fun List<ValueParameterDescriptor>.copyFor(
    newContainingDeclaration: CallableDescriptor,
    newTypeParameters: List<TypeParameterDescriptor>
): List<ValueParameterDescriptor> =
    map { it.copyFor(newContainingDeclaration, newTypeParameters) }