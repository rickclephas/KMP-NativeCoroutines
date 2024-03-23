package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.hidesFromObjCFqName
import com.rickclephas.kmp.nativecoroutines.compiler.utils.refinesInSwiftFqName
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass

internal val DeclarationDescriptor.isRefined: Boolean
    get() = annotations.any { annotation ->
        !annotation.isNativeCoroutinesAnnotation && annotation.isRefinementAnnotation
    }

private val AnnotationDescriptor.isRefinementAnnotation: Boolean
    get() = annotationClass?.annotations?.any { metaAnnotation ->
        val fqName = metaAnnotation.fqName
        fqName == hidesFromObjCFqName || fqName == refinesInSwiftFqName
    } ?: false
