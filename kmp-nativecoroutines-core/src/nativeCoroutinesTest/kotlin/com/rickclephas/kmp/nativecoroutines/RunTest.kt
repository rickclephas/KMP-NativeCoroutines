package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class TestResult

/**
 * There is a freezing [issue](https://github.com/Kotlin/kotlinx.coroutines/issues/3195)
 * with the [kotlinx.coroutines.test.runTest] function.
 *
 * As a temporary workaround this function uses `runBlocking` on native targets instead.
 * This is kind of hacky, but once we drop the old memory model this is no longer needed.
 */
// TODO: Remove temporary fix for runTest freezing issue
internal expect fun runTest(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): TestResult

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("SuspendFunctionOnCoroutineScope")
internal suspend fun CoroutineScope.runCurrent() {
    if (this is TestScope) {
        this.runCurrent()
    } else {
        coroutineContext[Job]?.children?.forEach { it.join() }
    }
}
