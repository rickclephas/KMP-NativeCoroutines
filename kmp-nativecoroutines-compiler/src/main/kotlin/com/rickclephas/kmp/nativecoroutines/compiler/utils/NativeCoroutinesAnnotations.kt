package com.rickclephas.kmp.nativecoroutines.compiler.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesFqNames as FqNames
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
private val nativeCoroutinesRefinedFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined")
private val nativeCoroutinesRefinedStateFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState")

internal class NativeCoroutinesAnnotations(descriptor: CallableDescriptor) {

    val nativeCoroutines: AnnotationDescriptor?
    val nativeCoroutineScope: AnnotationDescriptor?
    val nativeCoroutinesIgnore: AnnotationDescriptor?
    val nativeCoroutinesRefined: AnnotationDescriptor?
    val nativeCoroutinesRefinedState: AnnotationDescriptor?
    val nativeCoroutinesState: AnnotationDescriptor?

    init {
        var nativeCoroutines: AnnotationDescriptor? = null
        var nativeCoroutineScope: AnnotationDescriptor? = null
        var nativeCoroutinesIgnore: AnnotationDescriptor? = null
        var nativeCoroutinesRefined: AnnotationDescriptor? = null
        var nativeCoroutinesRefinedState: AnnotationDescriptor? = null
        var nativeCoroutinesState: AnnotationDescriptor? = null
        descriptor.annotations.forEach {
            when (it.fqName) {
                FqNames.nativeCoroutines -> nativeCoroutines = it
                FqNames.nativeCoroutineScope -> nativeCoroutineScope = it
                FqNames.nativeCoroutinesIgnore -> nativeCoroutinesIgnore = it
                nativeCoroutinesRefinedFqName -> nativeCoroutinesRefined = it
                nativeCoroutinesRefinedStateFqName -> nativeCoroutinesRefinedState = it
                FqNames.nativeCoroutinesState -> nativeCoroutinesState = it
            }
        }
        this.nativeCoroutines = nativeCoroutines
        this.nativeCoroutineScope = nativeCoroutineScope
        this.nativeCoroutinesIgnore = nativeCoroutinesIgnore
        this.nativeCoroutinesRefined = nativeCoroutinesRefined
        this.nativeCoroutinesRefinedState = nativeCoroutinesRefinedState
        this.nativeCoroutinesState = nativeCoroutinesState
    }
}
