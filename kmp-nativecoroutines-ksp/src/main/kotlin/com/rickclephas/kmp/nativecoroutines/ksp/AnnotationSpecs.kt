package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.canonicalClassName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec as toAnnotationSpecImpl

internal fun Sequence<KSAnnotation>.toAnnotationSpecs(
    objCName: String? = null,
    ignoredAnnotationNames: Set<String> = emptySet()
): List<AnnotationSpec> {
    val annotationSpecs = mutableListOf<AnnotationSpec>()
    var objCNameAnnotation: AnnotationSpec? = null
    for (annotation in this) {
        if (annotation.isObjCName) {
            objCNameAnnotation = annotation.toObjCNameAnnotationSpec(objCName ?: "")
            continue
        }
        annotation.toAnnotationSpec()
            .takeUnless { it.typeName.canonicalClassName in ignoredAnnotationNames }
            ?.let(annotationSpecs::add)
    }
    if (objCNameAnnotation == null && objCName != null) {
        objCNameAnnotation = ObjCNameAnnotationSpec(objCName, null)
    }
    objCNameAnnotation?.let(annotationSpecs::add)
    return annotationSpecs
}

private val KSAnnotation.isObjCName: Boolean get() {
    val classDeclaration = annotationType.resolve() as? KSClassDeclaration ?: return false
    return classDeclaration.packageName.asString() == objCNameAnnotationClassName.packageName &&
            classDeclaration.simpleName.asString() == objCNameAnnotationClassName.simpleName
}

private fun KSAnnotation.toObjCNameAnnotationSpec(objCName: String): AnnotationSpec {
    var name: String? = null
    var swiftName: String? = null
    for (argument in arguments) {
        val value = argument.value as? String ?: continue
        when (argument.name!!.getShortName()) {
            "name" -> name = value
            "swiftName" -> swiftName = value
        }
    }
    if (name == null) name = objCName
    return ObjCNameAnnotationSpec(name, swiftName)
}

@Suppress("FunctionName")
private fun ObjCNameAnnotationSpec(name: String?, swiftName: String?): AnnotationSpec =
    AnnotationSpec.builder(objCNameAnnotationClassName).apply {
        if (name != null)
            addMember("%N = %S", "name", name)
        if (swiftName != null)
            addMember("%N = %S", "swiftName", swiftName)
    }.build()

// TODO: Remove workaround for https://github.com/square/kotlinpoet/issues/1357
private fun KSAnnotation.toAnnotationSpec(): AnnotationSpec =
    KSAnnotationImpl(this).toAnnotationSpecImpl()

private class KSAnnotationImpl(private val annotation: KSAnnotation): KSAnnotation by annotation {
    override val arguments: List<KSValueArgument>
        get() = annotation.arguments.filter { it.value != null }
}
