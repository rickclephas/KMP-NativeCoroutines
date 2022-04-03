package com.rickclephas.kmp.nativecoroutines

import kotlin.native.concurrent.freeze

actual fun <T> T.freeze(): T = this.freeze()
