package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSCallableReference
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

internal sealed class CoroutinesType {
    abstract val typeReference: KSTypeReference
    sealed class Flow: CoroutinesType() {
        abstract val valueType: TypeName
        abstract val isNullable: Boolean
        abstract val isCustom: Boolean
        data class State(
            override val typeReference: KSTypeReference,
            override val valueType: TypeName,
            override val isNullable: Boolean,
            override val isCustom: Boolean,
            val mutable: Boolean
        ): Flow()
        data class Shared(
            override val typeReference: KSTypeReference,
            override val valueType: TypeName,
            override val isNullable: Boolean,
            override val isCustom: Boolean
        ): Flow()
        data class Generic(
            override val typeReference: KSTypeReference,
            override val valueType: TypeName,
            override val isNullable: Boolean,
            override val isCustom: Boolean
        ): Flow()
    }
    data class Function(
        override val typeReference: KSTypeReference,
        val isSuspend: Boolean,
        val receiverType: CoroutinesType?,
        val valueTypes: List<CoroutinesType>,
        val returnType: CoroutinesType
    ): CoroutinesType()
    data class Other(override val typeReference: KSTypeReference): CoroutinesType()
}

internal fun KSTypeReference.getCoroutinesType(
    typeParameterResolver: TypeParameterResolver,
    typeParameterArguments: Map<String, TypeName> = emptyMap(),
    typeIsMarkedNullable: Boolean? = null,
    typeIsSuperType: Boolean = false
): CoroutinesType {
    val type = resolve()
    if (type.isError) throw DeferSymbolException()
    val element = element
    if (element is KSCallableReference) {
        return CoroutinesType.Function(
            this, type.isSuspendFunctionType,
            element.receiverType?.getCoroutinesType(typeParameterResolver),
            element.functionParameters.map { it.type.getCoroutinesType(typeParameterResolver) },
            element.returnType.getCoroutinesType(typeParameterResolver)
        )
    }
    val classDeclaration = type.declaration as? KSClassDeclaration ?: return CoroutinesType.Other(this)
    val typeArguments = type.arguments.map { typeArgument ->
        val typeArgumentType = typeArgument.type?.resolve()?.takeUnless { it.isError } ?: throw DeferSymbolException()
        val typeArgumentDeclaration = typeArgumentType.declaration
        if (typeArgumentDeclaration !is KSTypeParameter) return@map typeArgument.toTypeName(typeParameterResolver)
        typeParameterArguments[typeArgumentDeclaration.name.getShortName()]?.let {
            it.copy(nullable = it.isNullable || typeArgumentType.isMarkedNullable)
        } ?: typeArgument.toTypeName(typeParameterResolver)
    }
    val isNullable = typeIsMarkedNullable ?: type.isMarkedNullable
    val flowType = classDeclaration.asFlowType()
    if (flowType != null) {
        val valueType = typeArguments.first()
        return when (flowType) {
            FlowType.MUTABLE_STATE, FlowType.STATE -> CoroutinesType.Flow.State(
                this, valueType, isNullable, typeIsSuperType, flowType == FlowType.MUTABLE_STATE
            )
            FlowType.SHARED -> CoroutinesType.Flow.Shared(
                this, valueType, isNullable, typeIsSuperType
            )
            FlowType.GENERIC, FlowType.ABSTRACT -> CoroutinesType.Flow.Generic(
                this, valueType, isNullable, typeIsSuperType || flowType != FlowType.GENERIC
            )
        }
    }
    val arguments = classDeclaration.typeParameters.map { it.name.getShortName() }.zip(typeArguments).toMap()
    for (superType in classDeclaration.superTypes) {
        val returnType = superType.getCoroutinesType(typeParameterResolver, arguments, isNullable, true)
        if (returnType !is CoroutinesType.Other) return returnType
    }
    return CoroutinesType.Other(this)
}
