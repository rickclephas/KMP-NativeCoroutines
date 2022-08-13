package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.*
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

internal fun KSFunctionDeclaration.toNativeCoroutinesFunSpec(nativeSuffix: String): FunSpec? {
    val typeParameterResolver = getTypeParameterResolver()
    val classDeclaration = parentDeclaration as? KSClassDeclaration

    val builder = FunSpec.builder("${simpleName.asString()}$nativeSuffix")
    docString?.trim()?.let(builder::addKdoc)
    builder.addAnnotations(annotations.toAnnotationSpecs(setOf(nativeCoroutinesAnnotationName, throwsAnnotationName)))
    // TODO: Add context receivers once supported
    builder.addModifiers(KModifier.PUBLIC)

    classDeclaration?.typeParameters?.toTypeVariableNames(typeParameterResolver)?.also(builder::addTypeVariables)
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

    // Convert the return type
    var returnTypeName = when (returnType) {
        is ReturnType.Flow -> nativeFlowClassName.parameterizedBy(returnType.valueType)
        else -> returnType.typeReference.toTypeName(typeParameterResolver)
    }
    if (isSuspend) {
        returnTypeName = nativeSuspendClassName.parameterizedBy(returnTypeName)
    }
    returnTypeName = returnTypeName.copy(annotations = returnType.typeReference.annotations.toAnnotationSpecs())
    builder.returns(returnTypeName)

    // Generate the function body
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
    code += "(${parameters.joinToString { "%N" }})"
    if (receiverParameter != null) {
        codeArgs.add(0, runMemberName)
        codeArgs.add(1, receiverParameter)
        code = "%M { %N.$code }"
    }
    if (returnType is ReturnType.Flow) {
        codeArgs.add(asNativeFlowMemberName)
        code = "$code.%M()"
    }
    if (isSuspend) {
        codeArgs.add(0, nativeSuspendMemberName)
        code = "%M { $code }"
    }
    code = "return $code"
    builder.addCode(code, *codeArgs.toTypedArray())

    return builder.build()
}
