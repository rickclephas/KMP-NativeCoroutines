package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.asNativeFlow

fun FlowIntegrationTests.getFlowNative(count: Int, delay: Long) =
    getFlow(count, delay).asNativeFlow(coroutineScope)

fun FlowIntegrationTests.getFlowWithNullNative(count: Int, nullIndex: Int, delay: Long) =
    getFlowWithNull(count, nullIndex, delay).asNativeFlow(coroutineScope)

fun FlowIntegrationTests.getFlowWithExceptionNative(count: Int, exceptionIndex: Int, message: String, delay: Long) =
    getFlowWithException(count, exceptionIndex, message, delay).asNativeFlow(coroutineScope)

fun FlowIntegrationTests.getFlowWithErrorNative(count: Int, errorIndex: Int, message: String, delay: Long) =
    getFlowWithError(count, errorIndex, message, delay).asNativeFlow(coroutineScope)

fun FlowIntegrationTests.getFlowWithCallbackNative(count: Int, callbackIndex: Int, delay: Long, callback: () -> Unit) =
    getFlowWithCallback(count, callbackIndex, delay, callback).asNativeFlow(coroutineScope)