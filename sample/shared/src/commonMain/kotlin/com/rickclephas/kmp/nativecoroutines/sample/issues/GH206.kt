package com.rickclephas.kmp.nativecoroutines.sample.issues

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect class GH206 {
    @NativeCoroutines
    public val property: Flow<String>
}
