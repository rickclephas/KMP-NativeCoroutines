package com.rickclephas.kmp.nativecoroutines

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HidesFromObjC
import kotlin.native.ShouldRefineInSwift

/**
 * Identifies `StateFlow` properties that require a native [ShouldRefineInSwift] state version.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@OptIn(ExperimentalObjCRefinement::class)
@HidesFromObjC
annotation class NativeCoroutinesRefinedState
