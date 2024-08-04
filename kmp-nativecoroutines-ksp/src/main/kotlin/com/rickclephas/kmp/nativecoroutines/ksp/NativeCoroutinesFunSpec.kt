package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.*
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

internal fun KSFunctionDeclaration.toNativeCoroutinesFunSpec(
    scopeProperty: CoroutineScopeProvider.ScopeProperty,
    options: KmpNativeCoroutinesOptions,
    shouldRefine: Boolean
): FunSpec {
    val typeParameterResolver = getTypeParameterResolver()
    val classDeclaration = parentDeclaration as? KSClassDeclaration

    val simpleName = simpleName.asString()
    val builder = FunSpec.builder("$simpleName${options.suffix}")
    docString?.trim()?.let(builder::addKdoc)
    builder.addAnnotations(annotations.toAnnotationSpecs(
        objCName = simpleName,
        ignoredAnnotationNames = setOf(
            nativeCoroutinesAnnotationName,
            nativeCoroutinesRefinedAnnotationName,
            throwsAnnotationName
        )
    ))
    if (shouldRefine) builder.addAnnotation(shouldRefineInSwiftAnnotationClassName)
    // TODO: Add context receivers once those are exported to ObjC
    builder.addModifiers(KModifier.PUBLIC)

    classDeclaration?.typeParameters?.toTypeVariableNames(typeParameterResolver, true)?.let(builder::addTypeVariables)
    val typeParameters = typeParameters.toTypeVariableNames(typeParameterResolver, true).also(builder::addTypeVariables)

    val extensionReceiver = extensionReceiver
    var receiverParameter: ParameterSpec? = null
    if (classDeclaration != null) {
        builder.receiver(classDeclaration.toTypeName(typeParameterResolver))
        if (extensionReceiver != null) {
            val type = extensionReceiver.toTypeName(typeParameterResolver)
            receiverParameter = ParameterSpec.builder("receiver", type)
                .addAnnotation(ObjCNameAnnotationSpec(null, "_"))
                .build().also(builder::addParameter)
        }
    } else if (extensionReceiver != null) {
        builder.receiver(extensionReceiver.toTypeName(typeParameterResolver))
    }
    val parameters = parameters.toParameterSpecs(typeParameterResolver).also(builder::addParameters)

    val returnType = returnType?.getCoroutinesType(typeParameterResolver) ?: throw DeferSymbolException()
    val isSuspend = modifiers.contains(Modifier.SUSPEND)

    val codeArgs = mutableListOf<Any>()
    var code = when (classDeclaration) {
        null -> {
            val isExtension = extensionReceiver != null
            codeArgs.add(MemberName(packageName.asString(), simpleName, isExtension))
            "%M"
        }
        else -> {
            codeArgs.add(simpleName)
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

    var returnTypeName: TypeName
    if (returnType is CoroutinesType.Flow) {
        val valueType = returnType.valueType.orNativeUnit
        returnTypeName = nativeFlowClassName.parameterizedBy(valueType).copy(nullable = returnType.isNullable)
        codeArgs.add(asNativeFlowMemberName)
        val genericParam = if (!valueType.isNativeUnit) {
            codeArgs.add(valueType)
            "<%T>"
        } else {
            ""
        }
        scopeProperty.codeArg.let(codeArgs::addAll)
        if (returnType.isNullable) code += "?"
        code = "$code.%M$genericParam(${scopeProperty.code})"
    } else {
        returnTypeName = returnType.typeReference.toTypeName(typeParameterResolver)
    }
    if (isSuspend) {
        val valueType = returnTypeName.orNativeUnit
        returnTypeName = nativeSuspendClassName.parameterizedBy(valueType)
        scopeProperty.codeArg.let { codeArgs.addAll(0, it) }
        val genericParam = if (!valueType.isNativeUnit) {
            codeArgs.add(0, valueType)
            "<%T>"
        } else {
            ""
        }
        codeArgs.add(0, nativeSuspendMemberName)
        code = "%M$genericParam(${scopeProperty.code}) { $code }"
    }
    returnTypeName = returnTypeName.copy(annotations = returnType.typeReference.annotations.toAnnotationSpecs())
    builder.returns(returnTypeName)
    code = "return $code"
    builder.addCode(code, *codeArgs.toTypedArray())

    containingFile?.let(builder::addOriginatingKSFile)
    scopeProperty.containingFile?.let(builder::addOriginatingKSFile)
    return builder.build()
}
