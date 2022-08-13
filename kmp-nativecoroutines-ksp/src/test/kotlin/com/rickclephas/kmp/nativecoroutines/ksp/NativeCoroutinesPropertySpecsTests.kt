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

    @Test
    fun nullableFlowValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow

        @NativeCoroutines
        val nullableFlowValue: Flow<String?> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableFlowValueNative: NativeFlow<String?>
          get() = nullableFlowValue.asNativeFlow()
    """.trimIndent())

    @Test
    fun nullableSharedFlowValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow

        @NativeCoroutines
        val nullableSharedFlowValue: SharedFlow<String?> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val nullableSharedFlowValueNative: NativeFlow<String?>
          get() = nullableSharedFlowValue.asNativeFlow()
        
        public val nullableSharedFlowValueNativeReplayCache: List<String?>
          get() = nullableSharedFlowValue.replayCache
    """.trimIndent())

    @Test
    fun nullableStateFlowValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow

        @NativeCoroutines
        val nullableStateFlowValue: StateFlow<String?> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableStateFlowValueNative: NativeFlow<String?>
          get() = nullableStateFlowValue.asNativeFlow()
        
        public val nullableStateFlowValueNativeValue: String?
          get() = nullableStateFlowValue.value
    """.trimIndent())

    @Test
    fun nullableFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow

        @NativeCoroutines
        val nullableFlow: Flow<String>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableFlowNative: NativeFlow<String>?
          get() = nullableFlow?.asNativeFlow()
    """.trimIndent())

    @Test
    fun nullableSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow

        @NativeCoroutines
        val nullableSharedFlow: SharedFlow<String>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val nullableSharedFlowNative: NativeFlow<String>?
          get() = nullableSharedFlow?.asNativeFlow()
        
        public val nullableSharedFlowNativeReplayCache: List<String>?
          get() = nullableSharedFlow?.replayCache
    """.trimIndent())

    @Test
    fun nullableStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow

        @NativeCoroutines
        val nullableStateFlow: StateFlow<String>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableStateFlowNative: NativeFlow<String>?
          get() = nullableStateFlow?.asNativeFlow()
        
        public val nullableStateFlowNativeValue: String?
          get() = nullableStateFlow?.value
    """.trimIndent())

    @Test
    fun nullableFlowAndValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow

        @NativeCoroutines
        val nullableFlowAndValue: Flow<String?>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableFlowAndValueNative: NativeFlow<String?>?
          get() = nullableFlowAndValue?.asNativeFlow()
    """.trimIndent())

    @Test
    fun nullableSharedFlowAndValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow

        @NativeCoroutines
        val nullableSharedFlowAndValue: SharedFlow<String?>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val nullableSharedFlowAndValueNative: NativeFlow<String?>?
          get() = nullableSharedFlowAndValue?.asNativeFlow()
        
        public val nullableSharedFlowAndValueNativeReplayCache: List<String?>?
          get() = nullableSharedFlowAndValue?.replayCache
    """.trimIndent())

    @Test
    fun nullableStateFlowAndValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow

        @NativeCoroutines
        val nullableStateFlowAndValue: StateFlow<String?>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableStateFlowAndValueNative: NativeFlow<String?>?
          get() = nullableStateFlowAndValue?.asNativeFlow()
        
        public val nullableStateFlowAndValueNativeValue: String?
          get() = nullableStateFlowAndValue?.value
    """.trimIndent())
}
