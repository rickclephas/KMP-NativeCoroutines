package com.rickclephas.kmp.nativecoroutines.gradle

open class KmpNativeCoroutinesExtension {
    /**
     * The suffix used to generate the native coroutine function and property names.
     */
    var suffix: String = "Native"
    /**
     * The suffix used to generate the native coroutine file names.
     * Note: defaults to [suffix] when `null`.
     */
    var fileSuffix: String? = null
}
