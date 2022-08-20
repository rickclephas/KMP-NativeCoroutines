package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
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
            override val nullable: Boolean
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
    typeParameterArguments: Map<String, KSTypeArgument> = emptyMap()
): ReturnType? {
    val type = resolve()
    if (type.isError) return null
    val classDeclaration = type.declaration as? KSClassDeclaration ?: return ReturnType.Other(this)
    val typeArguments = type.arguments.map { typeArgument ->
        val declaration = typeArgument.type?.resolve()?.takeUnless { it.isError }?.declaration ?: return null
        if (declaration !is KSTypeParameter) return@map typeArgument
        typeParameterArguments[declaration.name.getShortName()] ?: typeArgument
    }
    if (classDeclaration.isStateFlow())
        return ReturnType.Flow.State(
            this,
            typeArguments.first().toTypeName(typeParameterResolver),
            type.isMarkedNullable)
    if (classDeclaration.isSharedFlow())
        return ReturnType.Flow.Shared(
            this,
            typeArguments.first().toTypeName(typeParameterResolver),
            type.isMarkedNullable)
    if (classDeclaration.isFlow())
        return ReturnType.Flow.Generic(
            this,
            typeArguments.first().toTypeName(typeParameterResolver),
            type.isMarkedNullable)
    val arguments = classDeclaration.typeParameters.map { it.name.getShortName() }.zip(typeArguments).toMap()
    for (superType in classDeclaration.superTypes) {
        val returnType = superType.getReturnType(typeParameterResolver, arguments)
        if (returnType == null || returnType is ReturnType.Flow) return returnType
    }
    return ReturnType.Other(this)
}

private const val coroutinesPackageName = "kotlinx.coroutines.flow"

private fun KSClassDeclaration.isStateFlow(): Boolean {
    if (packageName.asString() != coroutinesPackageName) return false
    val simpleName = simpleName.asString()
    return simpleName == "StateFlow" || simpleName == "MutableStateFlow"
}

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
