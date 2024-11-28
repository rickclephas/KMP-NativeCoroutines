package com.rickclephas.kmp.nativecoroutines

/**
 * A callback with no arguments.
 */
public typealias NativeCallback = () -> NativeUnit?

/**
 * A callback with a single argument.
 */
public typealias NativeCallback1<T> = (T) -> NativeUnit?

/**
 * A no-op function that has the signature of a [NativeCallback1].
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
internal fun <T> EmptyNativeCallback1(value: T): NativeUnit? = null

/**
 * A callback with two arguments.
 */
public typealias NativeCallback2<T1, T2> = (T1, T2) -> NativeUnit?

/**
 * A no-op function that has the signature of a [NativeCallback2].
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
internal fun <T1, T2> EmptyNativeCallback2(value1: T1, value2: T2): NativeUnit? = null
