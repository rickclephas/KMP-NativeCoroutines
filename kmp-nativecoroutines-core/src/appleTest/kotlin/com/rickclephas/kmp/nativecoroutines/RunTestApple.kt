package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual typealias TestResult = Unit

internal actual fun runTest(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): TestResult = runBlocking(context, block)
