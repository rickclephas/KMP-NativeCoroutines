package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.Job

/**
 * A function that cancels the coroutines [Job].
 */
public typealias NativeCancellable<Unit> = () -> Unit

/**
 * Creates a [NativeCancellable] for this [Job].
 *
 * The returned cancellable will cancel the job without a cause.
 * @see Job.cancel
 */
internal inline fun Job.asNativeCancellable(): NativeCancellable<Unit> = { cancel() }
