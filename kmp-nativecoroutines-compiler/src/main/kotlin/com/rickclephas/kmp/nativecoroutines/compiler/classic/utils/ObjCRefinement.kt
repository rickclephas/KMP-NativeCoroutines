package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.FqNames
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass

internal val DeclarationDescriptor.isRefined: Boolean
    get() = annotations.any { annotation ->
        !annotation.isNativeCoroutinesAnnotation && annotation.isRefinementAnnotation
    }

@Suppress("UnstableApiUsage")
private val AnnotationDescriptor.isRefinementAnnotation: Boolean
    get() = annotationClass?.annotations?.any { metaAnnotation ->
        val fqName = metaAnnotation.fqName
        fqName == FqNames.hidesFromObjC || fqName == FqNames.refinesInSwift
    } ?: false
