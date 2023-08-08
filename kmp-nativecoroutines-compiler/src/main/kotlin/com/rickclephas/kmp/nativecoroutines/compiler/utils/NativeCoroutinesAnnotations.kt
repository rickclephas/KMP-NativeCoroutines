package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName

private val nativeCoroutinesFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutines")
private val nativeCoroutineScopeFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope")
private val nativeCoroutinesIgnoreFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore")
private val nativeCoroutinesRefinedFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined")
private val nativeCoroutinesRefinedStateFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState")
private val nativeCoroutinesStateFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState")

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
                nativeCoroutinesFqName -> nativeCoroutines = it
                nativeCoroutineScopeFqName -> nativeCoroutineScope = it
                nativeCoroutinesIgnoreFqName -> nativeCoroutinesIgnore = it
                nativeCoroutinesRefinedFqName -> nativeCoroutinesRefined = it
                nativeCoroutinesRefinedStateFqName -> nativeCoroutinesRefinedState = it
                nativeCoroutinesStateFqName -> nativeCoroutinesState = it
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
