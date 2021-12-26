package com.rickclephas.kmp.nativecoroutines

import kotlin.reflect.KClass

/**
 * Defines what exceptions should be propagated as `NSError`s.
 *
 * Exceptions which are instances of one of the [exceptionClasses] or their subclasses,
 * are propagated as a `NSError`s.
 * Other Kotlin exceptions are considered unhandled and cause program termination.
 *
 * @see Throws
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class NativeCoroutineThrows(vararg val exceptionClasses: KClass<out Throwable>)