package com.rickclephas.kmp.nativecoroutines

import platform.Foundation.NSError
import kotlin.coroutines.cancellation.CancellationException
import kotlin.native.concurrent.freeze
import kotlin.reflect.KClass
import com.rickclephas.kmp.nserrorkt.throwAsNSError

actual typealias NativeError = NSError

/**
 * Uses Kotlin Native runtime functions to convert a [Throwable] to a [NSError].
 *
 * Warning: [Throwable]s that aren't of a [propagatedExceptions] type will terminate the program.
 * Note: [CancellationException]s are always propagated.
 *
 * @param propagatedExceptions an array of [Throwable] types that should be propagated as [NSError]s.
 */
internal actual fun Throwable.asNativeError(
    propagatedExceptions: Array<KClass<out Throwable>>
): NSError = freeze().throwAsNSError(*propagatedExceptions.run {
    if (contains(CancellationException::class)) this
    else plus(CancellationException::class)
})
