package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

internal data class CallableSignature(
    val callableId: CallableId,
    val isSuspend: Boolean,
    val valueParameters: List<Pair<Name, Type>>,
    val returnType: Type
) {
    sealed class Type {
        abstract val rawType: Raw

        sealed class Flow: Type() {
            abstract val valueType: Type

            data class Simple(
                override val rawType: Raw,
                override val valueType: Type
            ): Flow()
            data class Shared(
                override val rawType: Raw,
                override val valueType: Type
            ): Flow()
            data class State(
                override val rawType: Raw,
                override val valueType: Type,
                val isMutable: Boolean
            ): Flow()
        }

        sealed class Raw: Type() {
            final override val rawType: Raw get() = this
            // TODO: without ConeKotlinType?
            abstract val type: ConeKotlinType

            data class Simple(override val type: ConeKotlinType): Raw()
        }
    }
}
