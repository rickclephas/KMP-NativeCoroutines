package com.rickclephas.kmp.nativecoroutines.compiler.utils

internal abstract class CallableSignatureBuilder<T, R>(
    private val build: (CallableSignature, List<T>) -> R
) {
    protected abstract val T.isNullable: Boolean

    private val rawTypes = mutableListOf<T>()

    private val T.rawTypeIndex: Int get() {
        rawTypes.add(this)
        return rawTypes.lastIndex
    }

    fun T.asRawType(isInput: Boolean): CallableSignature.Type.Raw =
        CallableSignature.Type.Raw(rawTypeIndex, isNullable, isInput)

    fun T.asSimpleFlow(
        isInput: Boolean,
        valueType: CallableSignature.Type,
        isCustom: Boolean
    ): CallableSignature.Type.Flow.Simple =
        CallableSignature.Type.Flow.Simple(rawTypeIndex, isNullable, isInput, valueType, isCustom)

    fun T.asSharedFlow(
        isInput: Boolean,
        valueType: CallableSignature.Type
    ): CallableSignature.Type.Flow.Shared =
        CallableSignature.Type.Flow.Shared(rawTypeIndex, isNullable, isInput, valueType)

    fun T.asStateFlow(
        isInput: Boolean,
        valueType: CallableSignature.Type,
        isMutable: Boolean
    ): CallableSignature.Type.Flow.State =
        CallableSignature.Type.Flow.State(rawTypeIndex, isNullable, isInput, valueType, isMutable)

    fun T.asFunction(
        isInput: Boolean,
        isSuspend: Boolean,
        receiverType: CallableSignature.Type?,
        valueTypes: List<CallableSignature.Type>,
        returnType: CallableSignature.Type
    ): CallableSignature.Type.Function =
        CallableSignature.Type.Function(rawTypeIndex, isNullable, isInput, isSuspend, receiverType, valueTypes, returnType)

    fun build(signature: CallableSignature): R = build(signature, rawTypes)
}
