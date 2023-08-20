package com.rickclephas.kmp.nativecoroutines

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HidesFromObjC
import kotlin.native.ShouldRefineInSwift

/**
 * Identifies properties and functions that require a native [ShouldRefineInSwift] coroutines version.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@OptIn(ExperimentalObjCRefinement::class)
@HidesFromObjC
public annotation class NativeCoroutinesRefined
