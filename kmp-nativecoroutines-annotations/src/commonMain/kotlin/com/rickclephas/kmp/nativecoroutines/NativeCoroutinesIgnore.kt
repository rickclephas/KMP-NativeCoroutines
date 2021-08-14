package com.rickclephas.kmp.nativecoroutines

/**
 * Identifies properties and functions that are ignored by the NativeCoroutines compiler.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class NativeCoroutinesIgnore