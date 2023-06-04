package com.rickclephas.kmp.nativecoroutines.sample

import platform.Foundation.NSDate
import platform.Foundation.date
import platform.Foundation.timeIntervalSince1970

internal actual fun epochSeconds(): Long = NSDate.date().timeIntervalSince1970().toLong()
