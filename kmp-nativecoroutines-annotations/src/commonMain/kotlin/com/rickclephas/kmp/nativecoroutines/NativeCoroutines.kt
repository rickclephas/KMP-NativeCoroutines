package com.rickclephas.kmp.nativecoroutines

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HidesFromObjC

/**
 * Identifies properties and functions that require a native coroutines version.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@OptIn(ExperimentalObjCRefinement::class)
@HidesFromObjC
public annotation class NativeCoroutines
