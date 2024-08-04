package com.rickclephas.kmp.nativecoroutines.compiler.utils

@Suppress("DuplicatedCode")
internal fun CallableSignature.orNull(): CallableSignature? {
    if (isSuspend) return this
    if (receiverType != null && receiverType !is CallableSignature.Type.Raw) return this
    if (valueTypes.any { it !is CallableSignature.Type.Raw }) return this
    if (returnType !is CallableSignature.Type.Raw) return this
    return null
}

@Suppress("DuplicatedCode")
internal fun CallableSignature.Type.Function.orRaw(): CallableSignature.Type {
    if (isSuspend) return this
    if (receiverType != null && receiverType !is CallableSignature.Type.Raw) return this
    if (valueTypes.any { it !is CallableSignature.Type.Raw }) return this
    if (returnType !is CallableSignature.Type.Raw) return this
    return asRaw()
}

internal fun CallableSignature.forK2Mode(k2Mode: Boolean): CallableSignature {
    if (k2Mode) return this
    val returnType = when (returnType) {
        is CallableSignature.Type.Flow -> returnType
        else -> returnType.asRaw()
    }
    return CallableSignature(
        isSuspend,
        receiverType?.asRaw(),
        valueTypes.map { it.asRaw() },
        returnType
    )
}

private fun CallableSignature.Type.asRaw(): CallableSignature.Type.Raw {
    if (this is CallableSignature.Type.Raw) return this
    return CallableSignature.Type.Raw(rawTypeIndex, isNullable, isInput)
}

private fun CallableSignature.Type.isOrHas(
    predicate: CallableSignature.Type.(isInput: Boolean) -> Boolean
): Boolean = when (this) {
    is CallableSignature.Type.Raw -> predicate(isInput)
    is CallableSignature.Type.Flow -> predicate(isInput)
    is CallableSignature.Type.Function -> predicate(isInput) ||
            receiverType?.isOrHas(predicate) == true ||
            valueTypes.any { it.isOrHas(predicate) } ||
            returnType.isOrHas(predicate)
}

internal val CallableSignature.Type.isUnsupportedInputFlow: Boolean get() = when (this) {
    is CallableSignature.Type.Raw -> false
    is CallableSignature.Type.Flow.Simple -> isCustom
    is CallableSignature.Type.Flow -> true
    is CallableSignature.Type.Function -> false
}

internal val CallableSignature.hasUnsupportedInputFlow: Boolean
    get() = receiverType?.isOrHas { isInput && isUnsupportedInputFlow } == true ||
            valueTypes.any { it.isOrHas { isInput && isUnsupportedInputFlow } } ||
            returnType.isOrHas { isInput && isUnsupportedInputFlow }

internal val CallableSignature.Type.isSuspend: Boolean get() = when (this) {
    is CallableSignature.Type.Raw -> false
    is CallableSignature.Type.Flow -> false
    is CallableSignature.Type.Function -> isSuspend
}

internal val CallableSignature.Type.isOrHasSuspend: Boolean get() = isOrHas { isSuspend }

internal val CallableSignature.Type.isFlow: Boolean get() = when (this) {
    is CallableSignature.Type.Raw -> false
    is CallableSignature.Type.Flow -> true
    is CallableSignature.Type.Function -> false
}

internal val CallableSignature.Type.isOrHasFlow: Boolean get() = isOrHas { isFlow }
