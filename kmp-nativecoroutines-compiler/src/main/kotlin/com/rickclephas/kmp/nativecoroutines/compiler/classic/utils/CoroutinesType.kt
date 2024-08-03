package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesType
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.typeUtil.supertypes

internal val CallableDescriptor.coroutinesType: CoroutinesType?
    get() = returnTypeOrNothing.getCoroutinesType(CoroutinesTypeConstructors(module))

private fun KotlinType.getCoroutinesType(
    typeConstructors: CoroutinesTypeConstructors
): CoroutinesType? {
    val isFunctionType = isFunctionType
    val isSuspendFunctionType = isSuspendFunctionType
    if (isFunctionType || isSuspendFunctionType) {
        val receiverType = getReceiverTypeFromFunctionType()?.getCoroutinesType(typeConstructors)
        val valueTypes = getValueParameterTypesFromFunctionType().map { it.type.getCoroutinesType(typeConstructors) }
        val returnType = getReturnTypeFromFunctionType().getCoroutinesType(typeConstructors)
        if (!isSuspendFunctionType && receiverType == null && valueTypes.all { it == null } && returnType == null) return null
        return CoroutinesType.Function(isSuspendFunctionType, receiverType, valueTypes, returnType)
    }
    if (constructor == typeConstructors.stateFlow) return CoroutinesType.Flow.State(false)
    if (constructor == typeConstructors.flow) return CoroutinesType.Flow.Generic(false)
    supertypes().forEach {
        if (it.constructor == typeConstructors.stateFlow) return CoroutinesType.Flow.State(true)
        if (it.constructor == typeConstructors.flow) return CoroutinesType.Flow.Generic(true)
    }
    return null
}

private class CoroutinesTypeConstructors(module: ModuleDescriptor) {
    val stateFlow: TypeConstructor = module.findTypeConstructor(ClassIds.stateFlow)
    val flow: TypeConstructor = module.findTypeConstructor(ClassIds.flow)
}

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
