package com.rickclephas.kmp.nativecoroutines

/**
 * A callback with a single argument.
 *
 * We don't want the Swift code to known how to get the [Unit] object, so we'll provide it as the second argument.
 * This way Swift can just return the value that it received without knowing what it is/how to get it.
 */
typealias NativeCallback<T> = (T, Unit) -> Unit

/**
 * Invokes the callback with the specified [value].
 */
internal inline operator fun <T> NativeCallback<T>.invoke(value: T) =
    invoke(value.freeze(), Unit)

/**
 * A callback with two arguments.
 *
 * We don't want the Swift code to known how to get the [Unit] object, so we'll provide it as the third argument.
 * This way Swift can just return the value that it received without knowing what it is/how to get it.
 */
typealias NativeCallback2<T1, T2> = (T1, T2, Unit) -> Unit

/**
 * Invokes the callback with the specified [value1] and [value2].
 */
internal inline operator fun <T1, T2> NativeCallback2<T1, T2>.invoke(value1: T1, value2: T2) =
    invoke(value1.freeze(), value2.freeze(), Unit)