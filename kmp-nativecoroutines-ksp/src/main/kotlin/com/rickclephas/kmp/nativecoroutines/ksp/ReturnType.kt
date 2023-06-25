package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

internal sealed class ReturnType {
    abstract val typeReference: KSTypeReference
    sealed class Flow: ReturnType() {
        abstract val valueType: TypeName
        abstract val nullable: Boolean
        class State(
            override val typeReference: KSTypeReference,
            override val valueType: TypeName,
            override val nullable: Boolean,
            val mutable: Boolean
        ): Flow()
        class Shared(
            override val typeReference: KSTypeReference,
            override val valueType: TypeName,
            override val nullable: Boolean
        ): Flow()
        class Generic(
            override val typeReference: KSTypeReference,
            override val valueType: TypeName,
            override val nullable: Boolean
        ): Flow()
    }
    class Other(override val typeReference: KSTypeReference): ReturnType()
}

internal fun KSTypeReference.getReturnType(
    typeParameterResolver: TypeParameterResolver,
    typeParameterArguments: Map<String, TypeName> = emptyMap(),
    typeIsMarkedNullable: Boolean? = null
): ReturnType? {
    val type = resolve()
    if (type.isError) return null
    val classDeclaration = type.declaration as? KSClassDeclaration ?: return ReturnType.Other(this)
    val typeArguments = type.arguments.map { typeArgument ->
        val typeArgumentType = typeArgument.type?.resolve()?.takeUnless { it.isError } ?: return null
        val typeArgumentDeclaration = typeArgumentType.declaration
        if (typeArgumentDeclaration !is KSTypeParameter) return@map typeArgument.toTypeName(typeParameterResolver)
        typeParameterArguments[typeArgumentDeclaration.name.getShortName()]?.let {
            it.copy(nullable = it.isNullable || typeArgumentType.isMarkedNullable)
        } ?: typeArgument.toTypeName(typeParameterResolver)
    }
    val isMarkedNullable = typeIsMarkedNullable ?: type.isMarkedNullable
    if (classDeclaration.isMutableStateFlow())
        return ReturnType.Flow.State(
            this,
            typeArguments.first(),
            isMarkedNullable,
            true)
    if (classDeclaration.isStateFlow())
        return ReturnType.Flow.State(
            this,
            typeArguments.first(),
            isMarkedNullable,
            false)
    if (classDeclaration.isSharedFlow())
        return ReturnType.Flow.Shared(
            this,
            typeArguments.first(),
            isMarkedNullable)
    if (classDeclaration.isFlow())
        return ReturnType.Flow.Generic(
            this,
            typeArguments.first(),
            isMarkedNullable)
    val arguments = classDeclaration.typeParameters.map { it.name.getShortName() }.zip(typeArguments).toMap()
    for (superType in classDeclaration.superTypes) {
        val returnType = superType.getReturnType(typeParameterResolver, arguments, isMarkedNullable)
        if (returnType == null || returnType is ReturnType.Flow) return returnType
    }
    return ReturnType.Other(this)
}

private const val coroutinesPackageName = "kotlinx.coroutines.flow"

private fun KSClassDeclaration.isMutableStateFlow(): Boolean =
    packageName.asString() == coroutinesPackageName && simpleName.asString() == "MutableStateFlow"

private fun KSClassDeclaration.isStateFlow(): Boolean =
    packageName.asString() == coroutinesPackageName && simpleName.asString() == "StateFlow"

private fun KSClassDeclaration.isSharedFlow(): Boolean {
    if (packageName.asString() != coroutinesPackageName) return false
    val simpleName = simpleName.asString()
    return simpleName == "SharedFlow" || simpleName == "MutableSharedFlow"
}

private fun KSClassDeclaration.isFlow(): Boolean {
    if (packageName.asString() != coroutinesPackageName) return false
    val simpleName = simpleName.asString()
    return simpleName == "Flow" || simpleName == "AbstractFlow"
}
