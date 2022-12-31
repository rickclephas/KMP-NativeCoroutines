package com.rickclephas.kmp.nativecoroutines.compiler.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesFqNames as FqNames
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor

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
                FqNames.nativeCoroutines -> nativeCoroutines = it
                FqNames.nativeCoroutinesIgnore -> nativeCoroutinesIgnore = it
                FqNames.nativeCoroutinesState -> nativeCoroutinesState = it
                FqNames.nativeCoroutineScope -> nativeCoroutineScope = it
            }
        }
        this.nativeCoroutines = nativeCoroutines
        this.nativeCoroutinesIgnore = nativeCoroutinesIgnore
        this.nativeCoroutinesState = nativeCoroutinesState
        this.nativeCoroutineScope = nativeCoroutineScope
    }
}
