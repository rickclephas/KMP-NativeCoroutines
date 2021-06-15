package com.rickclephas.kmp.nativecoroutines.sample

import kotlin.native.concurrent.freeze

actual fun <T> T.freeze() = freeze()