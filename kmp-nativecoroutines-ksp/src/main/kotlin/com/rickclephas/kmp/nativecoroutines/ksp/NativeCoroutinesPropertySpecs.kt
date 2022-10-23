package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.*
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

internal fun KSPropertyDeclaration.toNativeCoroutinesPropertySpecs(
    scopeProperty: CoroutineScopeProvider.ScopeProperty,
    nativeSuffix: String
): List<PropertySpec>? {
    val typeParameterResolver = getTypeParameterResolver()
    val type = type.getReturnType(typeParameterResolver) ?: return null
    if (type !is ReturnType.Flow) error("Only Flow properties are supported")
    return buildList {
        add(toNativeCoroutinesPropertySpec(scopeProperty, nativeSuffix, typeParameterResolver, type))
        if (type is ReturnType.Flow.State)
            add(toNativeCoroutinesValuePropertySpec(nativeSuffix, typeParameterResolver, type))
        else if (type is ReturnType.Flow.Shared)
            add(toNativeCoroutinesReplayCachePropertySpec(nativeSuffix, typeParameterResolver, type))
    }
}

private fun KSPropertyDeclaration.toNativeCoroutinesPropertySpec(
    scopeProperty: CoroutineScopeProvider.ScopeProperty,
    nativeSuffix: String,
    typeParameterResolver: TypeParameterResolver,
    type: ReturnType.Flow
): PropertySpec {
    var typeName: TypeName = nativeFlowClassName.parameterizedBy(type.valueType).copy(nullable = type.nullable)
    typeName = typeName.copy(annotations = type.typeReference.annotations.toAnnotationSpecs())
    val name = "${simpleName.asString()}$nativeSuffix"
    return createPropertySpec(typeParameterResolver, name, typeName) { code, codeArgs ->
        codeArgs.add(asNativeFlowMemberName)
        scopeProperty.codeArg?.let(codeArgs::add)
        addCode("return $code${if(type.nullable) "?." else "."}%M(${scopeProperty.code})", *codeArgs.toTypedArray())
    }.apply {
        scopeProperty.containingFile?.let(::addOriginatingKSFile)
    }.build()
}

private fun KSPropertyDeclaration.toNativeCoroutinesValuePropertySpec(
    nativeSuffix: String,
    typeParameterResolver: TypeParameterResolver,
    type: ReturnType.Flow.State
): PropertySpec {
    var typeName = type.valueType.copy(annotations = type.typeReference.annotations.toAnnotationSpecs())
    if (type.nullable) typeName = typeName.copy(nullable = true)
    val name = "${simpleName.asString()}${nativeSuffix}Value"
    return createPropertySpec(typeParameterResolver, name, typeName) { code, codeArgs ->
        addCode("return $code${if(type.nullable) "?." else "."}value", *codeArgs.toTypedArray())
    }.build()
}

private fun KSPropertyDeclaration.toNativeCoroutinesReplayCachePropertySpec(
    nativeSuffix: String,
    typeParameterResolver: TypeParameterResolver,
    type: ReturnType.Flow.Shared
): PropertySpec {
    var typeName: TypeName = LIST.parameterizedBy(type.valueType).copy(nullable = type.nullable)
    typeName = typeName.copy(annotations = type.typeReference.annotations.toAnnotationSpecs())
    val name = "${simpleName.asString()}${nativeSuffix}ReplayCache"
    return createPropertySpec(typeParameterResolver, name, typeName) { code, codeArgs ->
        addCode("return $code${if(type.nullable) "?." else "."}replayCache", *codeArgs.toTypedArray())
    }.build()
}

private fun KSPropertyDeclaration.createPropertySpec(
    typeParameterResolver: TypeParameterResolver,
    name: String,
    typeName: TypeName,
    addCode: FunSpec.Builder.(code: String, codeArgs: MutableList<Any>) -> Unit
): PropertySpec.Builder {
    val classDeclaration = parentDeclaration as? KSClassDeclaration

    val builder = PropertySpec.builder(name, typeName)
    docString?.trim()?.let(builder::addKdoc)
    builder.addAnnotations(annotations.toAnnotationSpecs(setOf(nativeCoroutinesAnnotationName, throwsAnnotationName)))
    // TODO: Add context receivers once those are exported to ObjC
    builder.addModifiers(KModifier.PUBLIC)

    classDeclaration?.typeParameters?.toTypeVariableNames(typeParameterResolver)?.let(builder::addTypeVariables)
    typeParameters.toTypeVariableNames(typeParameterResolver).let(builder::addTypeVariables)

    val extensionReceiver = extensionReceiver
    if (classDeclaration != null) {
        builder.receiver(classDeclaration.toTypeName(typeParameterResolver))
        if (extensionReceiver != null) error("Class extension properties aren't supported")
    } else if (extensionReceiver != null) {
        builder.receiver(extensionReceiver.toTypeName(typeParameterResolver))
    }

    val getterBuilder = FunSpec.getterBuilder()
    getter?.annotations?.toAnnotationSpecs(setOf(throwsAnnotationName))?.let(getterBuilder::addAnnotations)
    val codeArgs = mutableListOf<Any>()
    val code = when (classDeclaration) {
        null -> {
            val isExtension = extensionReceiver != null
            codeArgs.add(MemberName(packageName.asString(), simpleName.asString(), isExtension))
            "%M"
        }
        else -> {
            codeArgs.add(simpleName.asString())
            "%N"
        }
    }
    addCode(getterBuilder, code, codeArgs)
    builder.getter(getterBuilder.build())

    containingFile?.let(builder::addOriginatingKSFile)
    return builder
}
