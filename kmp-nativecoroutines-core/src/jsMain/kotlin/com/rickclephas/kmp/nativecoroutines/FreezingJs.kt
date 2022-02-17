package com.rickclephas.kmp.nativecoroutines

/**
 * Freezing is a no-op on JS
 */
internal actual fun <T> T.freeze() = this