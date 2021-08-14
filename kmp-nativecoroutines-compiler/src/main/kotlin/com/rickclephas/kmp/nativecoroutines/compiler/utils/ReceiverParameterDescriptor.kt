package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl

internal fun ReceiverParameterDescriptor.copyFor(
    newContainingDeclaration: CallableDescriptor,
    newTypeParameters: List<TypeParameterDescriptor>
): ReceiverParameterDescriptor {
    val value = value.let { it.replaceType(it.type.replaceFunctionGenerics(containingDeclaration, newTypeParameters)) }
    return ReceiverParameterDescriptorImpl(newContainingDeclaration, value, annotations)
}