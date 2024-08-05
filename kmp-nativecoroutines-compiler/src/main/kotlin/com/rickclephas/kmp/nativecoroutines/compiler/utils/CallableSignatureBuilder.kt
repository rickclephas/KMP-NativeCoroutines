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

    fun T.asRawType(): CallableSignature.Type.Raw =
        CallableSignature.Type.Raw(rawTypeIndex, isNullable)

    fun T.asSimpleFlow(
        valueType: CallableSignature.Type
    ): CallableSignature.Type.Flow.Simple =
        CallableSignature.Type.Flow.Simple(rawTypeIndex, isNullable, valueType)

    fun T.asSharedFlow(
        valueType: CallableSignature.Type
    ): CallableSignature.Type.Flow.Shared =
        CallableSignature.Type.Flow.Shared(rawTypeIndex, isNullable, valueType)

    fun T.asStateFlow(
        valueType: CallableSignature.Type,
        isMutable: Boolean
    ): CallableSignature.Type.Flow.State =
        CallableSignature.Type.Flow.State(rawTypeIndex, isNullable, valueType, isMutable)

    fun T.asFunction(
        isSuspend: Boolean,
        receiverType: CallableSignature.Type?,
        valueTypes: List<CallableSignature.Type>,
        returnType: CallableSignature.Type
    ): CallableSignature.Type.Function =
        CallableSignature.Type.Function(rawTypeIndex, isNullable, isSuspend, receiverType, valueTypes, returnType)

    fun build(signature: CallableSignature): R = build(signature, rawTypes)
}
