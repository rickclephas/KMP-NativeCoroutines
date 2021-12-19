package com.rickclephas.kmp.nativecoroutines.sample.utils

import kotlin.native.concurrent.freeze

actual fun <T> T.freeze() = freeze()