package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.*
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

internal fun KSFunctionDeclaration.toNativeCoroutinesFunSpec(
    scopeProperty: CoroutineScopeProvider.ScopeProperty,
    nativeSuffix: String
): FunSpec? {
    val typeParameterResolver = getTypeParameterResolver()
    val classDeclaration = parentDeclaration as? KSClassDeclaration

    val builder = FunSpec.builder("${simpleName.asString()}$nativeSuffix")
    docString?.trim()?.let(builder::addKdoc)
    builder.addAnnotations(annotations.toAnnotationSpecs(setOf(nativeCoroutinesAnnotationName, throwsAnnotationName)))
    // TODO: Add context receivers once those are exported to ObjC
    builder.addModifiers(KModifier.PUBLIC)

    classDeclaration?.typeParameters?.toTypeVariableNames(typeParameterResolver)?.let(builder::addTypeVariables)
    val typeParameters = typeParameters.toTypeVariableNames(typeParameterResolver).also(builder::addTypeVariables)

    val extensionReceiver = extensionReceiver
    var receiverParameter: ParameterSpec? = null
    if (classDeclaration != null) {
        builder.receiver(classDeclaration.toTypeName(typeParameterResolver))
        if (extensionReceiver != null) {
            val type = extensionReceiver.toTypeName(typeParameterResolver)
            receiverParameter = ParameterSpec.builder("receiver", type).build().also(builder::addParameter)
        }
    } else if (extensionReceiver != null) {
        builder.receiver(extensionReceiver.toTypeName(typeParameterResolver))
    }
    val parameters = parameters.toParameterSpecs(typeParameterResolver).also(builder::addParameters)

    val returnType = returnType?.getReturnType(typeParameterResolver) ?: return null
    val isSuspend = modifiers.contains(Modifier.SUSPEND)

    var returnTypeName = when (returnType) {
        is ReturnType.Flow -> nativeFlowClassName.parameterizedBy(returnType.valueType).copy(nullable = returnType.nullable)
        else -> returnType.typeReference.toTypeName(typeParameterResolver)
    }
    if (isSuspend) {
        returnTypeName = nativeSuspendClassName.parameterizedBy(returnTypeName)
    }
    returnTypeName = returnTypeName.copy(annotations = returnType.typeReference.annotations.toAnnotationSpecs())
    builder.returns(returnTypeName)

    val codeArgs = mutableListOf<Any>()
    var code = when (classDeclaration) {
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
    if (typeParameters.isNotEmpty()) {
        codeArgs.addAll(typeParameters.map { it.name })
        code += "<${typeParameters.joinToString { "%N" }}>"
    }
    codeArgs.addAll(parameters)
    code += "(${parameters.joinToString { if (KModifier.VARARG in it.modifiers) "*%N" else "%N" }})"
    if (receiverParameter != null) {
        codeArgs.add(0, runMemberName)
        codeArgs.add(1, receiverParameter)
        code = "%M { %N.$code }"
    }
    if (returnType is ReturnType.Flow) {
        codeArgs.add(asNativeFlowMemberName)
        scopeProperty.codeArg?.let(codeArgs::add)
        if (returnType.nullable) code += "?"
        code = "$code.%M(${scopeProperty.code})"
    }
    if (isSuspend) {
        codeArgs.add(0, nativeSuspendMemberName)
        scopeProperty.codeArg?.let { codeArgs.add(1, it) }
        code = "%M(${scopeProperty.code}) { $code }"
    }
    code = "return $code"
    builder.addCode(code, *codeArgs.toTypedArray())

    containingFile?.let(builder::addOriginatingKSFile)
    scopeProperty.containingFile?.let(builder::addOriginatingKSFile)
    return builder.build()
}
