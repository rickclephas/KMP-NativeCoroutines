package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.typeUtil.supertypes

internal val CallableDescriptor.coroutinesReturnType: CoroutinesReturnType? get() {
    val returnType = returnTypeOrNothing
    val stateFlowConstructor = module.findTypeConstructor(CoroutinesClassIds.stateFlow)
    if (returnType.constructor == stateFlowConstructor) return CoroutinesReturnType.Flow.State
    val flowConstructor = module.findTypeConstructor(CoroutinesClassIds.flow)
    if (returnType.constructor == flowConstructor) return CoroutinesReturnType.Flow.Generic
    returnType.supertypes().forEach {
        if (it.constructor == stateFlowConstructor) return CoroutinesReturnType.Flow.State
        if (it.constructor == flowConstructor) return CoroutinesReturnType.Flow.Generic
        if (it.constructor == coroutineScopeConstructor) return CoroutinesReturnType.CoroutineScope
    }
    return null
}

private fun ModuleDescriptor.findTypeConstructor(classId: ClassId): TypeConstructor =
    findClassifierAcrossModuleDependencies(classId)?.typeConstructor
        ?: throw NoSuchElementException("Couldn't find ${classId.relativeClassName} constructor")

internal val CallableDescriptor.isCoroutineScopeProperty: Boolean get() {
    if (this !is PropertyDescriptor) return false
    val returnType = returnTypeOrNothing
    val coroutineScope = module.findTypeConstructor(CoroutinesClassIds.coroutineScope)
    if (returnType.constructor == coroutineScope) return true
    return returnType.supertypes().any { it.constructor == coroutineScope }
}
