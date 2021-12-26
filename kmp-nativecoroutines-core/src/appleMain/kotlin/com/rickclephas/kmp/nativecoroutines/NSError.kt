package com.rickclephas.kmp.nativecoroutines

import kotlinx.cinterop.*
import platform.Foundation.NSError
import kotlin.native.concurrent.freeze
import kotlin.native.internal.GCUnsafeCall
import kotlin.reflect.KClass

/**
 * Uses Kotlin Native runtime functions to convert a [Throwable] to a [NSError].
 *
 * Warning: [Throwable]s that aren't of a [propagatedExceptions] type will terminate the program.
 *
 * @param propagatedExceptions an array of [Throwable] types that should be propagated as [NSError]s.
 */
internal fun Throwable.asNSError(
    propagatedExceptions: Array<KClass<out Throwable>>
): NSError {
    freeze()
    val shouldPropagate = propagatedExceptions.any { it.isInstance(this) }
    return memScoped {
        val error = alloc<ObjCObjectVar<NSError>>()
        val types = when (shouldPropagate) {
            true -> allocArray<CPointerVar<*>>(2).apply {
                val typeInfo = getTypeInfo(this@asNSError)
                set(0, interpretCPointer<CPointed>(typeInfo))
            }
            false -> allocArray(1)
        }
        rethrowExceptionAsNSError(this@asNSError, error.ptr, types)
        error.value
    }
}

@GCUnsafeCall("Kotlin_Any_getTypeInfo")
private external fun getTypeInfo(obj: Any): NativePtr

@GCUnsafeCall("Kotlin_ObjCExport_RethrowExceptionAsNSError")
private external fun rethrowExceptionAsNSError(
    exception: Throwable,
    error: CPointer<ObjCObjectVar<NSError>>,
    types: CArrayPointer<CPointerVar<*>>
)