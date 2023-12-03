package com.rickclephas.kmp.nativecoroutines.compiler.utils

internal sealed class CoroutinesReturnType private constructor() {
    sealed class Flow private constructor(): CoroutinesReturnType() {
        data object Generic: Flow()
        data object State: Flow()
    }
    data class Function(
        val isSuspend: Boolean,
        val receiverType: CoroutinesReturnType?,
        val valueTypes: List<CoroutinesReturnType?>,
        val returnType: CoroutinesReturnType?
    ): CoroutinesReturnType()
}

internal val CoroutinesReturnType?.hasSuspend: Boolean get() {
    if (this !is CoroutinesReturnType.Function) return false
    if (isSuspend) return true
    if (receiverType.hasSuspend) return true
    if (returnType.hasSuspend) return true
    return valueTypes.any { it.hasSuspend }
}

internal val CoroutinesReturnType?.hasFlow: Boolean get() {
    if (this is CoroutinesReturnType.Flow) return true
    if (this !is CoroutinesReturnType.Function) return false
    if (receiverType.hasFlow) return true
    if (returnType.hasFlow) return true
    return valueTypes.any { it.hasFlow }
}

internal fun CoroutinesReturnType?.hasUnsupportedInputFlow(isInput: Boolean): Boolean {
    if (this is CoroutinesReturnType.Flow.Generic) return false
    if (this is CoroutinesReturnType.Flow) return isInput
    if (this !is CoroutinesReturnType.Function) return false
    if (receiverType.hasUnsupportedInputFlow(!isInput)) return true
    if (returnType.hasUnsupportedInputFlow(isInput)) return true
    return valueTypes.any { it.hasUnsupportedInputFlow(!isInput) }
}
