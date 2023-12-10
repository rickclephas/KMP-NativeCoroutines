package com.rickclephas.kmp.nativecoroutines.compiler.utils

internal sealed class CoroutinesType private constructor() {
    sealed class Flow private constructor(): CoroutinesType() {
        abstract val isCustom: Boolean
        data class Generic(override val isCustom: Boolean): Flow()
        data class State(override val isCustom: Boolean): Flow()
    }
    data class Function(
        val isSuspend: Boolean,
        val receiverType: CoroutinesType?,
        val valueTypes: List<CoroutinesType?>,
        val returnType: CoroutinesType?
    ): CoroutinesType()
}

internal val CoroutinesType?.hasSuspend: Boolean get() {
    if (this !is CoroutinesType.Function) return false
    if (isSuspend) return true
    if (receiverType.hasSuspend) return true
    if (returnType.hasSuspend) return true
    return valueTypes.any { it.hasSuspend }
}

internal val CoroutinesType?.hasFlow: Boolean get() {
    if (this is CoroutinesType.Flow) return true
    if (this !is CoroutinesType.Function) return false
    if (receiverType.hasFlow) return true
    if (returnType.hasFlow) return true
    return valueTypes.any { it.hasFlow }
}

internal fun CoroutinesType?.hasUnsupportedInputFlow(isInput: Boolean): Boolean {
    if (this is CoroutinesType.Flow.Generic) return isCustom && isInput
    if (this is CoroutinesType.Flow) return isInput
    if (this !is CoroutinesType.Function) return false
    if (receiverType.hasUnsupportedInputFlow(!isInput)) return true
    if (returnType.hasUnsupportedInputFlow(isInput)) return true
    return valueTypes.any { it.hasUnsupportedInputFlow(!isInput) }
}
