package com.rickclephas.kmp.nativecoroutines

/**
 * Identifies properties and functions that require a native coroutines version.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class NativeCoroutines
