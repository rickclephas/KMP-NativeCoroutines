package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.typeUtil.supertypes

private fun ModuleDescriptor.findTypeConstructor(classId: ClassId): TypeConstructor =
    findClassifierAcrossModuleDependencies(classId)?.typeConstructor
        ?: throw NoSuchElementException("Couldn't find ${classId.relativeClassName} constructor")

internal val CallableDescriptor.isCoroutineScopeProperty: Boolean get() {
    if (this !is PropertyDescriptor) return false
    val returnType = returnTypeOrNothing
    val coroutineScope = module.findTypeConstructor(ClassIds.coroutineScope)
    if (returnType.constructor == coroutineScope) return true
    return returnType.supertypes().any { it.constructor == coroutineScope }
}
