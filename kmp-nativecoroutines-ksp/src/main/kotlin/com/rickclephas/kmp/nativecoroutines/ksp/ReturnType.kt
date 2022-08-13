package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
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

internal fun KSTypeReference.getReturnType(typeParameterResolver: TypeParameterResolver): ReturnType? {
    val type = resolve()
    if (type.isError) return null
    val classDeclaration = type.declaration as? KSClassDeclaration ?: return ReturnType.Other(this)
    if (classDeclaration.isStateFlow())
        return ReturnType.Flow.State(
            this,
            type.arguments.first().toTypeName(typeParameterResolver),
            type.isMarkedNullable)
    if (classDeclaration.isSharedFlow())
        return ReturnType.Flow.Shared(
            this,
            type.arguments.first().toTypeName(typeParameterResolver),
            type.isMarkedNullable)
    if (classDeclaration.isFlow())
        return ReturnType.Flow.Generic(
            this,
            type.arguments.first().toTypeName(typeParameterResolver),
            type.isMarkedNullable)
    // TODO: Support Flow subclasses
    return ReturnType.Other(this)
}

private const val coroutinesPackageName = "kotlinx.coroutines.flow"

private fun KSClassDeclaration.isStateFlow(): Boolean =
    packageName.asString() == coroutinesPackageName && simpleName.asString() == "StateFlow"

private fun KSClassDeclaration.isSharedFlow(): Boolean =
    packageName.asString() == coroutinesPackageName && simpleName.asString() == "SharedFlow"

private fun KSClassDeclaration.isFlow(): Boolean =
    packageName.asString() == coroutinesPackageName && simpleName.asString() == "Flow"
