package com.rickclephas.kmp.nativecoroutines.ksp

import org.junit.Test

class NativeCoroutinesPropertySpecsTests: CompilationTests() {

    @Test
    fun globalFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow

        @NativeCoroutines
        val globalFlow: Flow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val globalFlowNative: NativeFlow<String>
          get() = globalFlow.asNativeFlow()
    """.trimIndent())

    @Test
    fun globalSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow

        @NativeCoroutines
        val globalSharedFlow: SharedFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val globalSharedFlowNative: NativeFlow<String>
          get() = globalSharedFlow.asNativeFlow()
        
        public val globalSharedFlowNativeReplayCache: List<String>
          get() = globalSharedFlow.replayCache
    """.trimIndent())

    @Test
    fun globalStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow

        @NativeCoroutines
        val globalStateFlow: StateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val globalStateFlowNative: NativeFlow<String>
          get() = globalStateFlow.asNativeFlow()
        
        public val globalStateFlowNativeValue: String
          get() = globalStateFlow.value
    """.trimIndent())
}
