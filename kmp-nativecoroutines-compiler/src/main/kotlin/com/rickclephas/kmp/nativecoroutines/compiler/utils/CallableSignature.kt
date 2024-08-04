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
