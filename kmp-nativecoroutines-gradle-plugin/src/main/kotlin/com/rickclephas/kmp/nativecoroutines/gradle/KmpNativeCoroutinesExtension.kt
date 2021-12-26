package com.rickclephas.kmp.nativecoroutines.gradle

open class KmpNativeCoroutinesExtension {

    /**
     * The suffix used to generate the native function and property names.
     */
    var suffix: String = "Native"

    /**
     * The default array of [Throwable] types that should be propagated as `NSError`s.
     */
    var propagatedExceptions: Array<String> = arrayOf()

    /**
     * Indicates if the [Throws] annotation is used to generate the [propagatedExceptions] list.
     */
    var useThrowsAnnotation: Boolean = true
}