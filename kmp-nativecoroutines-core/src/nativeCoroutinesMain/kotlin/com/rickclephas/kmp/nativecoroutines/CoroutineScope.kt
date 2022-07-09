package com.rickclephas.kmp.nativecoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * The default [CoroutineScope] used if no specific scope is provided.
 */
internal val defaultCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)