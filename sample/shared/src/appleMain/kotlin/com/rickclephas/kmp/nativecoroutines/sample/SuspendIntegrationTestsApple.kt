package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.nativeSuspend

fun SuspendIntegrationTests.returnValueNative(value: Int, delay: Long) =
    nativeSuspend(coroutineScope) { returnValue(value, delay) }

fun SuspendIntegrationTests.returnNullNative(delay: Long) =
    nativeSuspend(coroutineScope) { returnNull(delay) }

fun SuspendIntegrationTests.throwExceptionNative(message: String, delay: Long) =
    nativeSuspend(coroutineScope) { throwException(message, delay) }

fun SuspendIntegrationTests.throwErrorNative(message: String, delay: Long) =
    nativeSuspend(coroutineScope) { throwError(message, delay) }

fun SuspendIntegrationTests.returnFromCallbackNative(delay: Long, callback: () -> Int) =
    nativeSuspend(coroutineScope) { returnFromCallback(delay, callback) }