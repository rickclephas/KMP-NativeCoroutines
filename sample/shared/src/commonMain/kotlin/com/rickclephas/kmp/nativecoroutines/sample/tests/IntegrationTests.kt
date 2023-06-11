package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

public abstract class IntegrationTests {

    private val job = SupervisorJob()
    @NativeCoroutineScope
    internal val coroutineScope = CoroutineScope(job + Dispatchers.Default)

    public val activeJobCount: Int
        get() = job.children.count { it.isActive }

    public val uncompletedJobCount: Int
        get() = job.children.count { !it.isCompleted }
}
