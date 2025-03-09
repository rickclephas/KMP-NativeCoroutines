package com.rickclephas.kmp.nativecoroutines.sample.issues

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual class GH206 {
    @NativeCoroutines
    public actual val property: Flow<String> = flowOf("GH206")
}
