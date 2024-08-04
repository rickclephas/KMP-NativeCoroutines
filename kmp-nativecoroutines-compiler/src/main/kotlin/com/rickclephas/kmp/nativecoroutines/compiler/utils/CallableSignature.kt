package com.rickclephas.kmp.nativecoroutines.compiler.utils

internal data class CallableSignature(
    val isSuspend: Boolean,
    val receiverType: Type?,
    val valueTypes: List<Type>,
    val returnType: Type
) {
    sealed class Type {
        abstract val rawTypeIndex: Int
        abstract val isNullable: Boolean
        abstract val isInput: Boolean

        sealed class Flow: Type() {
            abstract val valueType: Type

            data class Simple(
                override val rawTypeIndex: Int,
                override val isNullable: Boolean,
                override val isInput: Boolean,
                override val valueType: Type,
                val isCustom: Boolean
            ): Flow()
            data class Shared(
                override val rawTypeIndex: Int,
                override val isNullable: Boolean,
                override val isInput: Boolean,
                override val valueType: Type
            ): Flow()
            data class State(
                override val rawTypeIndex: Int,
                override val isNullable: Boolean,
                override val isInput: Boolean,
                override val valueType: Type,
                val isMutable: Boolean
            ): Flow()
        }

        data class Function(
            override val rawTypeIndex: Int,
            override val isNullable: Boolean,
            override val isInput: Boolean,
            val isSuspend: Boolean,
            val receiverType: Type?,
            val valueTypes: List<Type>,
            val returnType: Type
        ): Type()

        data class Raw(
            override val rawTypeIndex: Int,
            override val isNullable: Boolean,
            override val isInput: Boolean
        ): Type()
    }
}

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
