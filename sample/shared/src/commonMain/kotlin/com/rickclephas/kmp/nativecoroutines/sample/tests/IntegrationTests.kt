package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.concurrent.AtomicInt

public abstract class IntegrationTests {

    private val job = SupervisorJob()
    @NativeCoroutineScope
    internal open val coroutineScope = CoroutineScope(job + Dispatchers.Default)

    private val swiftExportJobCount: AtomicInt = AtomicInt(0)
    public var isTestingSwiftExport: Boolean = false

    internal fun <T> Flow<T>.withSwiftExportTracking(): Flow<T> {
        if (!isTestingSwiftExport) return this
        return onStart {
            swiftExportJobCount.incrementAndGet()
        }.onCompletion {
            swiftExportJobCount.decrementAndGet()
        }
    }

    internal suspend inline fun <R> withSwiftExportTracking(block: suspend () -> R): R {
        if (!isTestingSwiftExport) return block()
        try {
            swiftExportJobCount.incrementAndGet()
            return block()
        } finally {
            swiftExportJobCount.decrementAndGet()
        }
    }

    public val activeJobCount: Int
        get() = swiftExportJobCount.value + job.children.count { it.isActive }

    public val uncompletedJobCount: Int
        get() = swiftExportJobCount.value + job.children.count { !it.isCompleted }
}
