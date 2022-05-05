package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeConstructor

internal fun KotlinType.replaceFunctionGenerics(
    oldContainingDeclaration: DeclarationDescriptor,
    newTypeParameters: List<TypeParameterDescriptor>
): KotlinType {
    fun KotlinType.replaceFunctionGenerics(): KotlinType? {
        // Replace the type constructor
        var typeConstructor: TypeConstructor? = null
        val typeParameter = constructor.declarationDescriptor as? TypeParameterDescriptor
        if (typeParameter != null && typeParameter.containingDeclaration == oldContainingDeclaration)
            typeConstructor = newTypeParameters.first { it.index == typeParameter.index }.typeConstructor
        // Replace the arguments
        var shouldReplaceArguments = false
        val arguments = arguments.map { projection ->
            projection.type.replaceFunctionGenerics()?.let {
                shouldReplaceArguments = true
                projection.replaceType(it)
            } ?: projection
        }
        // Only create a new type if something was replaced
        if (typeConstructor == null && !shouldReplaceArguments) return null
        return KotlinTypeFactory.simpleType(attributes, typeConstructor ?: constructor, arguments, isMarkedNullable)
    }
    return this.replaceFunctionGenerics() ?: this
}