package com.rickclephas.kmp.nativecoroutines.sample

import com.rickclephas.kmp.nativecoroutines.nativeSuspend

fun RandomLettersGenerator.getRandomLettersNative(throwException: Boolean) =
    nativeSuspend { getRandomLetters(throwException) }