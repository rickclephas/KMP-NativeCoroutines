package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestResult
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

@Suppress("ACTUAL_WITHOUT_EXPECT", "ACTUAL_TYPE_ALIAS_TO_CLASS_WITH_DECLARATION_SITE_VARIANCE")
actual typealias TestResult = Promise<Unit>

internal actual fun runTest(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): TestResult = kotlinx.coroutines.test.runTest(context = context, testBody = block)
