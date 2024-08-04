package com.rickclephas.kmp.nativecoroutines.compiler.utils

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
