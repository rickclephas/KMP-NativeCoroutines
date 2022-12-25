package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.typeUtil.supertypes

internal sealed class CoroutinesReturnType private constructor() {
    object CoroutineScope: CoroutinesReturnType()
    sealed class Flow private constructor(): CoroutinesReturnType() {
        object Generic: Flow()
        object State: Flow()
    }
}

internal val CallableDescriptor.coroutinesReturnType: CoroutinesReturnType? get() {
    val returnType = returnTypeOrNothing
    val flowConstructor = module.findFlowConstructor()
    val stateFlowConstructor = module.findStateFlowConstructor()
    if (returnType.constructor == stateFlowConstructor) return CoroutinesReturnType.Flow.State
    if (returnType.constructor == flowConstructor) return CoroutinesReturnType.Flow.Generic
    val coroutineScopeConstructor = module.findCoroutineScopeConstructor()
    if (returnType.constructor == coroutineScopeConstructor) return CoroutinesReturnType.CoroutineScope
    returnType.supertypes().forEach {
        if (it.constructor == stateFlowConstructor) return CoroutinesReturnType.Flow.State
        if (it.constructor == flowConstructor) return CoroutinesReturnType.Flow.Generic
        if (it.constructor == coroutineScopeConstructor) return CoroutinesReturnType.CoroutineScope
    }
    return null
}
