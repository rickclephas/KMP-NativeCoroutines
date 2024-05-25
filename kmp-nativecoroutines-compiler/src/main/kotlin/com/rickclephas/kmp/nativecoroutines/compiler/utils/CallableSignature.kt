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
        // TODO: without ConeKotlinType?
        abstract val type: ConeKotlinType

        sealed class Flow: Type() {
            // TODO: without ConeKotlinType?
            abstract val valueType: ConeKotlinType
            data class Simple(
                override val type: ConeKotlinType,
                override val valueType: ConeKotlinType
            ): Flow()
            data class State(
                override val type: ConeKotlinType,
                override val valueType: ConeKotlinType
            ): Flow()
        }

        data class Other(override val type: ConeKotlinType) : Type()
    }
}
