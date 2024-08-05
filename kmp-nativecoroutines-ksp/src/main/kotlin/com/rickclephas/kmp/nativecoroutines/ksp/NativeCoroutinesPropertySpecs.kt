package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.*
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

internal fun KSPropertyDeclaration.toNativeCoroutinesPropertySpecs(
    scopeProperty: CoroutineScopeProvider.ScopeProperty,
    options: KmpNativeCoroutinesOptions,
    asState: Boolean,
    shouldRefine: Boolean
): List<PropertySpec> {
    val typeParameterResolver = getTypeParameterResolver()
    val type = type.getCoroutinesType(typeParameterResolver)
    if (type !is CoroutinesType.Flow) return emptyList() // Only Flow properties are supported
    return buildList {
        val flowSuffix = if (asState) options.stateFlowSuffix else options.suffix
        if (flowSuffix != null)
            toPropertySpec(scopeProperty, flowSuffix, !asState, typeParameterResolver, type, shouldRefine)?.let(::add)
        val valueSuffix = if (asState) options.stateSuffix else options.flowValueSuffix
        if (type is CoroutinesType.Flow.State && valueSuffix != null)
            toValuePropertySpec(valueSuffix, asState, typeParameterResolver, type, shouldRefine)?.let(::add)
        if (type is CoroutinesType.Flow.Shared && options.flowReplayCacheSuffix != null)
            toReplayCachePropertySpec(options.flowReplayCacheSuffix, typeParameterResolver, type, shouldRefine)?.let(::add)
    }
}

private fun KSPropertyDeclaration.toPropertySpec(
    scopeProperty: CoroutineScopeProvider.ScopeProperty,
    nameSuffix: String,
    setObjCName: Boolean,
    typeParameterResolver: TypeParameterResolver,
    type: CoroutinesType.Flow,
    shouldRefine: Boolean
): PropertySpec? {
    val valueType = type.valueType.orNativeUnit
    var typeName: TypeName = nativeFlowClassName.parameterizedBy(valueType).copy(nullable = type.isNullable)
    typeName = typeName.copy(annotations = type.typeReference.annotations.toAnnotationSpecs())
    val simpleName = simpleName.asString()
    val name = "$simpleName$nameSuffix"
    val objCName = if (setObjCName) simpleName else null
    return createPropertySpec(typeParameterResolver, name, objCName, typeName, shouldRefine, { code, codeArgs ->
        codeArgs.add(asNativeFlowMemberName)
        val genericParam = if (!valueType.isNativeUnit) {
            codeArgs.add(valueType)
            "<%T>"
        } else {
            ""
        }
        scopeProperty.codeArg.let(codeArgs::addAll)
        addCode("return $code${if(type.isNullable) "?." else "."}%M$genericParam(${scopeProperty.code})", *codeArgs.toTypedArray())
    })?.apply {
        scopeProperty.containingFile?.let(::addOriginatingKSFile)
    }?.build()
}

private fun KSPropertyDeclaration.toValuePropertySpec(
    nameSuffix: String,
    setObjCName: Boolean,
    typeParameterResolver: TypeParameterResolver,
    type: CoroutinesType.Flow.State,
    shouldRefine: Boolean
): PropertySpec? {
    var typeName = type.valueType.copy(annotations = type.typeReference.annotations.toAnnotationSpecs())
    if (type.isNullable) typeName = typeName.copy(nullable = true)
    val simpleName = simpleName.asString()
    val name = "$simpleName$nameSuffix"
    val objCName = if (setObjCName) simpleName else null
    return createPropertySpec(typeParameterResolver, name, objCName, typeName, shouldRefine, { code, codeArgs ->
        addCode("return $code${if(type.isNullable) "?." else "."}value", *codeArgs.toTypedArray())
    }, when (type.mutable) {
        false -> null
        else -> { code, codeArgs ->
            addCode("$code${if(type.isNullable) "?." else "."}value = value", *codeArgs.toTypedArray())
        }
    })?.build()
}

private fun KSPropertyDeclaration.toReplayCachePropertySpec(
    nameSuffix: String,
    typeParameterResolver: TypeParameterResolver,
    type: CoroutinesType.Flow.Shared,
    shouldRefine: Boolean
): PropertySpec? {
    var typeName: TypeName = LIST.parameterizedBy(type.valueType).copy(nullable = type.isNullable)
    typeName = typeName.copy(annotations = type.typeReference.annotations.toAnnotationSpecs())
    val simpleName = simpleName.asString()
    val name = "$simpleName$nameSuffix"
    return createPropertySpec(typeParameterResolver, name, null, typeName, shouldRefine, { code, codeArgs ->
        addCode("return $code${if(type.isNullable) "?." else "."}replayCache", *codeArgs.toTypedArray())
    })?.build()
}

private fun KSPropertyDeclaration.createPropertySpec(
    typeParameterResolver: TypeParameterResolver,
    name: String,
    objCName: String?,
    typeName: TypeName,
    shouldRefine: Boolean,
    addGetterCode: FunSpec.Builder.(code: String, codeArgs: MutableList<Any>) -> Unit,
    addSetterCode: (FunSpec.Builder.(code: String, codeArgs: MutableList<Any>) -> Unit)? = null
): PropertySpec.Builder? {
    val classDeclaration = parentDeclaration as? KSClassDeclaration

    val builder = PropertySpec.builder(name, typeName)
    docString?.trim()?.let(builder::addKdoc)
    builder.addAnnotations(annotations.toAnnotationSpecs(
        objCName = objCName,
        ignoredAnnotationNames = setOf(
            nativeCoroutinesAnnotationName,
            nativeCoroutinesStateAnnotationName,
            nativeCoroutinesRefinedAnnotationName,
            nativeCoroutinesRefinedStateAnnotationName,
            throwsAnnotationName
        )
    ))
    if (shouldRefine) builder.addAnnotation(shouldRefineInSwiftAnnotationClassName)
    // TODO: Add context receivers once those are exported to ObjC
    builder.addModifiers(KModifier.PUBLIC)

    classDeclaration?.typeParameters?.toTypeVariableNames(typeParameterResolver, true)?.let(builder::addTypeVariables)
    typeParameters.toTypeVariableNames(typeParameterResolver, true).let(builder::addTypeVariables)

    val extensionReceiver = extensionReceiver
    if (classDeclaration != null) {
        builder.receiver(classDeclaration.toTypeName(typeParameterResolver))
        if (extensionReceiver != null) return null // Class extension properties aren't supported
    } else if (extensionReceiver != null) {
        builder.receiver(extensionReceiver.toTypeName(typeParameterResolver))
    }

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

    val getterBuilder = FunSpec.getterBuilder()
    getter?.annotations?.toAnnotationSpecs(
        ignoredAnnotationNames = setOf(throwsAnnotationName)
    )?.let(getterBuilder::addAnnotations)
    addGetterCode(getterBuilder, code, codeArgs)
    builder.getter(getterBuilder.build())

    if (addSetterCode != null) {
        builder.mutable()
        val setterBuilder = FunSpec.setterBuilder()
        setter?.annotations?.toAnnotationSpecs(
            ignoredAnnotationNames = setOf(throwsAnnotationName)
        )?.let(setterBuilder::addAnnotations)
        setterBuilder.addParameter("value", typeName)
        addSetterCode(setterBuilder, code, codeArgs)
        builder.setter(setterBuilder.build())
    }

    containingFile?.let(builder::addOriginatingKSFile)
    return builder
}
