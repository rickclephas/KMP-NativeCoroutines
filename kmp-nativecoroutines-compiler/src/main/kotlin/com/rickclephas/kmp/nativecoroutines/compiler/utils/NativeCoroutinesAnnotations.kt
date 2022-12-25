package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName

private val nativeCoroutinesFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutines")
private val nativeCoroutinesIgnoreFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore")
private val nativeCoroutinesStateFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState")
private val nativeCoroutineScopeFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope")

internal class NativeCoroutinesAnnotations(descriptor: CallableDescriptor) {

    val nativeCoroutines: AnnotationDescriptor?
    val nativeCoroutinesIgnore: AnnotationDescriptor?
    val nativeCoroutinesState: AnnotationDescriptor?
    val nativeCoroutineScope: AnnotationDescriptor?

    init {
        var nativeCoroutines: AnnotationDescriptor? = null
        var nativeCoroutinesIgnore: AnnotationDescriptor? = null
        var nativeCoroutinesState: AnnotationDescriptor? = null
        var nativeCoroutineScope: AnnotationDescriptor? = null
        descriptor.annotations.forEach {
            when (it.fqName) {
                nativeCoroutinesFqName -> nativeCoroutines = it
                nativeCoroutinesIgnoreFqName -> nativeCoroutinesIgnore = it
                nativeCoroutinesStateFqName -> nativeCoroutinesState = it
                nativeCoroutineScopeFqName -> nativeCoroutineScope = it
            }
        }
        this.nativeCoroutines = nativeCoroutines
        this.nativeCoroutinesIgnore = nativeCoroutinesIgnore
        this.nativeCoroutinesState = nativeCoroutinesState
        this.nativeCoroutineScope = nativeCoroutineScope
    }
}
