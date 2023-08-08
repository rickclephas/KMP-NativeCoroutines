package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.rickclephas.kmp.nativecoroutines.ksp.kotlinpoet.canonicalClassName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec as toAnnotationSpecImpl

internal fun Sequence<KSAnnotation>.toAnnotationSpecs(
    objCName: String? = null,
    ignoredAnnotationNames: Set<String> = emptySet()
): List<AnnotationSpec> {
    val annotationSpecs = mutableListOf<AnnotationSpec>()
    var objCNameAnnotation: AnnotationSpec? = null
    for (annotation in this) {
        if (annotation.isAnnotationClass(objCNameAnnotationClassName)) {
            objCNameAnnotation = annotation.toObjCNameAnnotationSpec(objCName)
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

private fun KSAnnotation.isAnnotationClass(className: ClassName): Boolean =
    annotationType.resolve().toClassName() == className

private fun KSAnnotation.toObjCNameAnnotationSpec(objCName: String?): AnnotationSpec {
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

private fun KSAnnotation.toAnnotationSpec(): AnnotationSpec = when {
    isAnnotationClass(optInAnnotationClassName) -> toOptInAnnotationSpec()
    else -> toAnnotationSpecImpl()
}

/**
 * The following is prohibited by the Kotlin compiler:
 * ```
 * @OptIn(markerClass = arrayOf(A::class, B::class))
 * ```
 * instead we'll use the supported format:
 * ```
 * @OptIn(A::class, B::class)
 * ```
 */
private fun KSAnnotation.toOptInAnnotationSpec(): AnnotationSpec {
    val builder = toAnnotationSpecImpl().toBuilder()
    builder.members.clear()
    if (arguments.size != 1) error("Unsupported OptIn argument count")
    val markerClass = arguments[0].value as List<*>
    markerClass.forEach {
        val type = when (it) {
            is KSTypeAlias -> it.type.resolve()
            is KSType -> it
            else -> error("Unsupported OptIn argument type")
        }
        builder.addMember("%T::class", type.toClassName())
    }
    return builder.build()
}
