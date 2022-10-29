package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName

private val nativeCoroutinesFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutines")
private val nativeCoroutineScopeFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope")
private val nativeCoroutinesIgnoreFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore")

internal class NativeCoroutinesAnnotations(descriptor: CallableDescriptor) {

    val nativeCoroutines: AnnotationDescriptor?
    val nativeCoroutineScope: AnnotationDescriptor?
    val nativeCoroutinesIgnore: AnnotationDescriptor?

    init {
        var nativeCoroutines: AnnotationDescriptor? = null
        var nativeCoroutineScope: AnnotationDescriptor? = null
        var nativeCoroutinesIgnore: AnnotationDescriptor? = null
        descriptor.annotations.forEach {
            when (it.fqName) {
                nativeCoroutinesFqName -> nativeCoroutines = it
                nativeCoroutineScopeFqName -> nativeCoroutineScope = it
                nativeCoroutinesIgnoreFqName -> nativeCoroutinesIgnore = it
            }
        }
        this.nativeCoroutines = nativeCoroutines
        this.nativeCoroutineScope = nativeCoroutineScope
        this.nativeCoroutinesIgnore = nativeCoroutinesIgnore
    }
}
