package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.asFirExpression
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.Names
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

internal fun FirExtension.buildAnnotationsCopy(
    originalAnnotations: List<FirAnnotation>,
    objCName: String? = null,
    objCNameSuffix: String? = null,
): List<FirAnnotation> {
    val annotations = mutableListOf<FirAnnotation>()
    var objcNameAnnotation: FirAnnotation? = null
    for (annotation in originalAnnotations) {
        when (annotation.toAnnotationClassId(session)) {
            ClassIds.objCName -> objcNameAnnotation = annotation
            ClassIds.deprecated -> buildDeprecatedAnnotationCopy(annotation)?.let(annotations::add)
            else -> continue
        }
    }
    objcNameAnnotation = buildObjCNameAnnotationCopy(objcNameAnnotation, objCName, objCNameSuffix)
    objcNameAnnotation?.let(annotations::add)
    return annotations
}

internal fun buildAnnotation(
    classId: ClassId,
    arguments: Map<Name, FirExpression> = emptyMap()
) = buildAnnotation {
    annotationTypeRef = buildResolvedTypeRef {
        coneType = classId.constructClassLikeType()
    }
    argumentMapping = buildAnnotationArgumentMapping {
        mapping.putAll(arguments)
    }
}

private fun buildObjCNameAnnotationCopy(
    annotation: FirAnnotation?,
    objCName: String?,
    objCNameSuffix: String?
): FirAnnotation? {
    val originalArguments = annotation?.getArguments(
        Names.ObjCName.name,
        Names.ObjCName.swiftName,
    ) ?: emptyMap()
    var name = originalArguments[Names.ObjCName.name]?.getLiteralValue<String>() ?: objCName
    var swiftName = originalArguments[Names.ObjCName.swiftName]?.getLiteralValue<String>()
    if (objCNameSuffix != null) {
        name = name?.plus(objCNameSuffix)
        swiftName = swiftName?.plus(objCNameSuffix)
    }
    val arguments = buildMap {
        if (name?.isNotEmpty() == true) {
            put(Names.ObjCName.name, name.asFirExpression())
        }
        if (swiftName?.isNotEmpty() == true) {
            put(Names.ObjCName.swiftName, swiftName.asFirExpression())
        }
    }
    if (arguments.isEmpty()) return null
    return buildAnnotation(ClassIds.objCName, arguments)
}

private fun buildDeprecatedAnnotationCopy(annotation: FirAnnotation): FirAnnotation? {
    val originalArguments = annotation.getArguments(
        Names.Deprecated.message,
        Names.Deprecated.replaceWith,
        Names.Deprecated.level,
    )
    val message = originalArguments[Names.Deprecated.message]?.getLiteralValue<String>() ?: return null
    val level = originalArguments[Names.Deprecated.level]
    val arguments = buildMap {
        put(Names.Deprecated.message, message.asFirExpression())
        if (level != null) {
            put(Names.Deprecated.level, level)
        }
    }
    return buildAnnotation(ClassIds.deprecated, arguments)
}

private fun FirAnnotation.getArguments(vararg names: Name): Map<Name, FirExpression> {
    val mapping = argumentMapping.mapping
    if (mapping.isNotEmpty()) return mapping.filterKeys { it in names }
    if (this !is FirAnnotationCall) return emptyMap()
    val arguments = mutableMapOf<Name, FirExpression>()
    var index: Int? = -1
    for (argument in this.arguments) {
        index = when (argument) {
            is FirNamedArgumentExpression -> null
            else -> index?.plus(1)
        }
        if (argument is FirNamedArgumentExpression) {
            if (argument.name !in names) continue
            arguments[argument.name] = argument.expression
        } else if (index != null) {
            arguments[names[index]] = argument
        }
    }
    return arguments
}

private inline fun <reified T> FirExpression.getLiteralValue(): T? =
    (this as? FirLiteralExpression)?.value as? T
