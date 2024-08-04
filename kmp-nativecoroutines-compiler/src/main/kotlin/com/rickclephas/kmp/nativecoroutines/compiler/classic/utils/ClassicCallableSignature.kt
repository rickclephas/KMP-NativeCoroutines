package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignatureBuilder
import com.rickclephas.kmp.nativecoroutines.compiler.utils.FqNames
import com.rickclephas.kmp.nativecoroutines.compiler.utils.orRaw
import org.jetbrains.kotlin.backend.common.descriptors.isSuspend
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.builtIns
import org.jetbrains.kotlin.types.typeUtil.supertypes

internal class ClassicCallableSignature(
    val signature: CallableSignature,
    @Suppress("unused") private val rawTypes: List<KotlinType>
) {
    class Builder: CallableSignatureBuilder<KotlinType, ClassicCallableSignature>(::ClassicCallableSignature) {
        override val KotlinType.isNullable: Boolean get() = isMarkedNullable
    }

    companion object {
        operator fun invoke(block: Builder.() -> CallableSignature): ClassicCallableSignature =
            Builder().run { build(block()) }
    }
}

internal fun CallableDescriptor.getCallableSignature(): ClassicCallableSignature = ClassicCallableSignature {
    CallableSignature(
        isSuspend,
        extensionReceiverParameter?.type?.let { createType(it, isInput = true) },
        valueParameters.map { createType(it.returnTypeOrNothing, isInput = true) },
        createType(returnTypeOrNothing, isInput = false)
    )
}

private fun ClassicCallableSignature.Builder.createType(
    rawType: KotlinType,
    isInput: Boolean
): CallableSignature.Type {
    createFunctionType(rawType, isInput)?.let { return it }
    val types = sequence {
        yield(rawType)
        yieldAll(rawType.supertypes())
    }
    types.forEachIndexed { index, type ->
        when {
            KotlinBuiltIns.isTypeConstructorForGivenClass(type.constructor, FqNames.stateFlow.toUnsafe()) -> {
                val valueType = type.flowValueType.asRawType(isInput)
                return rawType.asStateFlow(isInput, valueType, isMutable = false)
            }
            KotlinBuiltIns.isTypeConstructorForGivenClass(type.constructor, FqNames.mutableStateFlow.toUnsafe()) -> {
                val valueType = type.flowValueType.asRawType(isInput)
                return rawType.asStateFlow(isInput, valueType, isMutable = true)
            }
            KotlinBuiltIns.isTypeConstructorForGivenClass(type.constructor, FqNames.sharedFlow.toUnsafe()) -> {
                val valueType = type.flowValueType.asRawType(isInput)
                return rawType.asSharedFlow(isInput, valueType)
            }
            KotlinBuiltIns.isTypeConstructorForGivenClass(type.constructor, FqNames.flow.toUnsafe()) -> {
                val valueType = type.flowValueType.asRawType(isInput)
                return rawType.asSimpleFlow(isInput, valueType, isCustom = index != 0)
            }
        }
    }
    return rawType.asRawType(isInput)
}

private fun ClassicCallableSignature.Builder.createFunctionType(
    rawType: KotlinType,
    isInput: Boolean
): CallableSignature.Type? {
    val isFunctionType = rawType.isFunctionType
    val isSuspendFunctionType = rawType.isSuspendFunctionType
    if (!isFunctionType && !isSuspendFunctionType) return null
    return rawType.asFunction(
        isInput,
        isSuspendFunctionType,
        rawType.getReceiverTypeFromFunctionType()?.let { createType(it, !isInput) },
        rawType.getValueParameterTypesFromFunctionType().map { createType(it.type, !isInput) },
        createType(rawType.getReturnTypeFromFunctionType(), isInput)
    ).orRaw()
}

private val KotlinType.flowValueType: KotlinType
    get() = arguments.firstOrNull()?.type ?: builtIns.nullableAnyType
