package com.rickclephas.kmp.nativecoroutines.sample

/**
 * Marks sample declarations that are only supported with the ObjC export
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class NativeCoroutinesObjCExport
