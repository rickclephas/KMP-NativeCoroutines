package com.rickclephas.kmp.nativecoroutines

/**
 * A callback with a single argument.
 *
 * We don't want the Swift code to know how to get the [Unit] object, so we'll provide it as the second argument.
 * This way Swift can just return the value that it received without knowing what it is/how to get it.
 */
public typealias NativeCallback<T> = (T, NativeUnit) -> NativeUnit

/**
 * Invokes the callback with the specified [value].
 */
internal inline operator fun <T> NativeCallback<T>.invoke(value: T) =
    invoke(value, NativeUnit)

/**
 * A callback with two arguments.
 *
 * We don't want the Swift code to know how to get the [Unit] object, so we'll provide it as the third argument.
 * This way Swift can just return the value that it received without knowing what it is/how to get it.
 */
public typealias NativeCallback2<T1, T2> = (T1, T2, NativeUnit) -> NativeUnit

/**
 * Invokes the callback with the specified [value1] and [value2].
 */
internal inline operator fun <T1, T2> NativeCallback2<T1, T2>.invoke(value1: T1, value2: T2) =
    invoke(value1, value2, NativeUnit)
