package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.name.Name

internal data class CallableSignature(
    val isSuspend: Boolean,
    val valueParameters: List<Pair<Name, Type>>,
    val returnType: Type
) {
    sealed class Type {
        abstract val rawTypeIndex: Int
        abstract val isNullable: Boolean

        sealed class Flow: Type() {
            abstract val valueType: Type

            data class Simple(
                override val rawTypeIndex: Int,
                override val isNullable: Boolean,
                override val valueType: Type
            ): Flow()
            data class Shared(
                override val rawTypeIndex: Int,
                override val isNullable: Boolean,
                override val valueType: Type
            ): Flow()
            data class State(
                override val rawTypeIndex: Int,
                override val isNullable: Boolean,
                override val valueType: Type,
                val isMutable: Boolean
            ): Flow()
        }

        data class Raw(
            override val rawTypeIndex: Int,
            override val isNullable: Boolean
        ): Type()
    }
}
