package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.nativeSuspend

fun IntegrationTests.returnValueNative(value: Int, delay: Long) =
    nativeSuspend(coroutineScope) { returnValue(value, delay) }

fun IntegrationTests.returnNullNative(delay: Long) =
    nativeSuspend(coroutineScope) { returnNull(delay) }

fun IntegrationTests.throwExceptionNative(message: String, delay: Long) =
    nativeSuspend(coroutineScope) { throwException(message, delay) }

fun IntegrationTests.throwErrorNative(message: String, delay: Long) =
    nativeSuspend(coroutineScope) { throwError(message, delay) }

fun IntegrationTests.returnFromCallbackNative(delay: Long, callback: () -> Int) =
    nativeSuspend(coroutineScope) { returnFromCallback(delay, callback) }