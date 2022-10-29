package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.typeUtil.supertypes

internal enum class CoroutinesReturnType {
    COROUTINE_SCOPE,
    FLOW
}

internal val CallableDescriptor.coroutinesReturnType: CoroutinesReturnType? get() {
    val returnType = returnTypeOrNothing
    val flowConstructor = module.findFlowConstructor()
    if (returnType.constructor == flowConstructor) return CoroutinesReturnType.FLOW
    val coroutineScopeConstructor = module.findCoroutineScopeConstructor()
    if (returnType.constructor == coroutineScopeConstructor) return CoroutinesReturnType.COROUTINE_SCOPE
    returnType.supertypes().forEach {
        if (it.constructor == flowConstructor) return CoroutinesReturnType.FLOW
        if (it.constructor == coroutineScopeConstructor) return CoroutinesReturnType.COROUTINE_SCOPE
    }
    return null
}
