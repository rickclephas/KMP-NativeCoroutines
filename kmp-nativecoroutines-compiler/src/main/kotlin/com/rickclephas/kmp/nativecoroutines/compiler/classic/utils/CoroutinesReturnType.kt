package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.typeUtil.supertypes

@Suppress("UnstableApiUsage")
internal val CallableDescriptor.coroutinesReturnType: CoroutinesReturnType? get() {
    val returnType = returnTypeOrNothing
    val stateFlowConstructor = module.findTypeConstructor(ClassIds.stateFlow)
    if (returnType.constructor == stateFlowConstructor) return CoroutinesReturnType.Flow.State
    val flowConstructor = module.findTypeConstructor(ClassIds.flow)
    if (returnType.constructor == flowConstructor) return CoroutinesReturnType.Flow.Generic
    val coroutineScopeConstructor = module.findTypeConstructor(ClassIds.coroutineScope)
    if (returnType.constructor == coroutineScopeConstructor) return CoroutinesReturnType.CoroutineScope
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
