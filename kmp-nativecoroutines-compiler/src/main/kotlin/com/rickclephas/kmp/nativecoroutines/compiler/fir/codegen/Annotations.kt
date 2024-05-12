package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.Names
import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fakeElement
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationCopy
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.ConstantValueKind

internal fun FirExtension.buildAnnotationsCopy(
    originalAnnotations: List<FirAnnotation>,
    objCName: String? = null,
    ignoredAnnotations: Set<ClassId> = emptySet()
): List<FirAnnotation> {
    val annotations = mutableListOf<FirAnnotation>()
    var objcNameAnnotation: FirAnnotation? = null
    for (annotation in originalAnnotations) {
        val classId = annotation.toAnnotationClassId(session)
        if (classId == ClassIds.objCName) {
            objcNameAnnotation = annotation
            continue
        }
        if (classId in ignoredAnnotations) continue
        annotations.add(buildAnnotationCopy(annotation) {
            source = annotation.source?.fakeElement(KtFakeSourceElementKind.PluginGenerated)
        })
    }
    if (objcNameAnnotation == null && objCName != null) {
        objcNameAnnotation = buildObjCNameAnnotation(objCName)
    }
    objcNameAnnotation?.let(annotations::add)
    return annotations
}

internal fun FirExtension.buildAnnotation(
    classId: ClassId,
    arguments: Map<Name, FirExpression> = emptyMap()
) = buildAnnotation {
    annotationTypeRef = buildResolvedTypeRef {
        type = classId.createConeType(session)
    }
    argumentMapping = buildAnnotationArgumentMapping {
        mapping.putAll(arguments)
    }
}

internal fun FirExtension.buildObjCNameAnnotation(
    name: String
) = buildAnnotation(ClassIds.objCName, mapOf(
    Names.ObjCName.name to buildLiteralExpression(null, ConstantValueKind.String, name, setType = true)
))
