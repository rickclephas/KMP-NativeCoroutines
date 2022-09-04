package com.rickclephas.kmp.nativecoroutines.ksp

import org.junit.Ignore
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
          get() = globalFlow.asNativeFlow(null)
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
          get() = globalSharedFlow.asNativeFlow(null)
        
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
          get() = globalStateFlow.asNativeFlow(null)
        
        public val globalStateFlowNativeValue: String
          get() = globalStateFlow.value
    """.trimIndent())

    @Test
    fun globalMutableStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.MutableStateFlow
        
        @NativeCoroutines
        val globalMutableStateFlow: MutableStateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val globalMutableStateFlowNative: NativeFlow<String>
          get() = globalMutableStateFlow.asNativeFlow(null)
        
        public val globalMutableStateFlowNativeValue: String
          get() = globalMutableStateFlow.value
    """.trimIndent())

    @Test
    fun globalCustomFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.AbstractFlow
        
        abstract class CustomFlow<T1, T2>: AbstractFlow<T2>()
        
        @NativeCoroutines
        val globalCustomFlow: CustomFlow<Int, String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val globalCustomFlowNative: NativeFlow<String>
          get() = globalCustomFlow.asNativeFlow(null)
    """.trimIndent())

    @Test
    fun nullableCustomFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.AbstractFlow
        
        abstract class CustomFlow<T1, T2>: AbstractFlow<T2>()
        
        @NativeCoroutines
        val nullableCustomFlow: CustomFlow<Int, String>? get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableCustomFlowNative: NativeFlow<String>?
          get() = nullableCustomFlow?.asNativeFlow(null)
    """.trimIndent())

    @Test
    fun nullableCustomFlowValueProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.AbstractFlow
        
        abstract class CustomFlow<T1, T2>: AbstractFlow<T2?>()
        
        @NativeCoroutines
        val nullableCustomFlowValue: CustomFlow<Int, String> get() = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val nullableCustomFlowValueNative: NativeFlow<String?>
          get() = nullableCustomFlowValue.asNativeFlow(null)
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
          get() = nullableFlowValue.asNativeFlow(null)
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
          get() = nullableSharedFlowValue.asNativeFlow(null)
        
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
          get() = nullableStateFlowValue.asNativeFlow(null)
        
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
          get() = nullableFlow?.asNativeFlow(null)
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
          get() = nullableSharedFlow?.asNativeFlow(null)
        
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
          get() = nullableStateFlow?.asNativeFlow(null)
        
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
          get() = nullableFlowAndValue?.asNativeFlow(null)
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
          get() = nullableSharedFlowAndValue?.asNativeFlow(null)
        
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
          get() = nullableStateFlowAndValue?.asNativeFlow(null)
        
        public val nullableStateFlowAndValueNativeValue: String?
          get() = nullableStateFlowAndValue?.value
    """.trimIndent())

    @Test
    fun genericSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        class MyClass<T>
        
        @NativeCoroutines
        val <T> MyClass<T>.genericSharedFlow: SharedFlow<T> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.collections.List
        
        public val <T> MyClass<T>.genericSharedFlowNative: NativeFlow<T>
          get() = genericSharedFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericSharedFlowNativeReplayCache: List<T>
          get() = genericSharedFlow.replayCache
    """.trimIndent())

    @Test
    fun genericStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        class MyClass<T>
        
        @NativeCoroutines
        val <T> MyClass<T>.genericStateFlow: StateFlow<T> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        
        public val <T> MyClass<T>.genericStateFlowNative: NativeFlow<T>
          get() = genericStateFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericStateFlowNativeValue: T
          get() = genericStateFlow.value
    """.trimIndent())

    @Test
    fun genericClassSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        class MyClass<T> {
            @NativeCoroutines
            val genericSharedFlow: SharedFlow<T> get() = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.collections.List
        
        public val <T> MyClass<T>.genericSharedFlowNative: NativeFlow<T>
          get() = genericSharedFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericSharedFlowNativeReplayCache: List<T>
          get() = genericSharedFlow.replayCache
    """.trimIndent())

    @Test
    fun genericClassStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        class MyClass<T> {
            @NativeCoroutines
            val genericStateFlow: StateFlow<T> get() = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        
        public val <T> MyClass<T>.genericStateFlowNative: NativeFlow<T>
          get() = genericStateFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericStateFlowNativeValue: T
          get() = genericStateFlow.value
    """.trimIndent())

    @Test
    fun kdocSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        /**
         * KDoc for [kdocSharedFlow]
         */
        @NativeCoroutines
        val kdocSharedFlow: SharedFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        /**
         * KDoc for [kdocSharedFlow]
         */
        public val kdocSharedFlowNative: NativeFlow<String>
          get() = kdocSharedFlow.asNativeFlow(null)
        
        /**
         * KDoc for [kdocSharedFlow]
         */
        public val kdocSharedFlowNativeReplayCache: List<String>
          get() = kdocSharedFlow.replayCache
    """.trimIndent())

    @Test
    fun kdocStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        /**
         * KDoc for [kdocStateFlow]
         */
        @NativeCoroutines
        val kdocStateFlow: StateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        /**
         * KDoc for [kdocStateFlow]
         */
        public val kdocStateFlowNative: NativeFlow<String>
          get() = kdocStateFlow.asNativeFlow(null)
        
        /**
         * KDoc for [kdocStateFlow]
         */
        public val kdocStateFlowNativeValue: String
          get() = kdocStateFlow.value
    """.trimIndent())

    @Test
    fun protectedOpenClassSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        open class MyClass {
            @NativeCoroutines
            protected val sharedFlow: SharedFlow<String> get() = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val MyClass.sharedFlowNative: NativeFlow<String>
          get() = sharedFlow.asNativeFlow(null)
        
        public val MyClass.sharedFlowNativeReplayCache: List<String>
          get() = sharedFlow.replayCache
    """.trimIndent())

    @Test
    fun protectedOpenClassStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        open class MyClass {
            @NativeCoroutines
            protected val stateFlow: StateFlow<String> get() = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val MyClass.stateFlowNative: NativeFlow<String>
          get() = stateFlow.asNativeFlow(null)
        
        public val MyClass.stateFlowNativeValue: String
          get() = stateFlow.value
    """.trimIndent())

    @Test
    fun extensionSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        @NativeCoroutines
        val String.sharedFlow: SharedFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val String.sharedFlowNative: NativeFlow<String>
          get() = sharedFlow.asNativeFlow(null)
        
        public val String.sharedFlowNativeReplayCache: List<String>
          get() = sharedFlow.replayCache
    """.trimIndent())

    @Test
    fun extensionStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        @NativeCoroutines
        val String.stateFlow: StateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val String.stateFlowNative: NativeFlow<String>
          get() = stateFlow.asNativeFlow(null)
        
        public val String.stateFlowNativeValue: String
          get() = stateFlow.value
    """.trimIndent())

    @Test
    fun implicitTypeProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.flow
        
        @NativeCoroutines
        val myFlow get() = flow { emit("") }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val myFlowNative: NativeFlow<String>
          get() = myFlow.asNativeFlow(null)
    """.trimIndent())

    @Test
    fun annotatedSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        @Deprecated("it's old")
        @get:ExperimentalStdlibApi
        @NativeCoroutines
        val sharedFlow: SharedFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.Deprecated
        import kotlin.DeprecationLevel
        import kotlin.ExperimentalStdlibApi
        import kotlin.ReplaceWith
        import kotlin.String
        import kotlin.collections.List
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        public val sharedFlowNative: NativeFlow<String>
          @get:ExperimentalStdlibApi
          get() = sharedFlow.asNativeFlow(null)
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        public val sharedFlowNativeReplayCache: List<String>
          @get:ExperimentalStdlibApi
          get() = sharedFlow.replayCache
    """.trimIndent())

    @Test
    fun annotatedStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        @Deprecated("it's old")
        @get:ExperimentalStdlibApi
        @NativeCoroutines
        val stateFlow: StateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.Deprecated
        import kotlin.DeprecationLevel
        import kotlin.ExperimentalStdlibApi
        import kotlin.ReplaceWith
        import kotlin.String
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        public val stateFlowNative: NativeFlow<String>
          @get:ExperimentalStdlibApi
          get() = stateFlow.asNativeFlow(null)
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        public val stateFlowNativeValue: String
          @get:ExperimentalStdlibApi
          get() = stateFlow.value
    """.trimIndent())

    /**
     * We can't test this since [Throws] is a typealias in Kotlin/JVM
     * which is where our KSP tests are currently running.
     */
    @Test
    @Ignore
    fun throwsAnnotationSharedFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.SharedFlow
        
        @get:Throws
        @NativeCoroutines
        val sharedFlow: SharedFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.collections.List
        
        public val sharedFlowNative: NativeFlow<String>
          get() = sharedFlow.asNativeFlow(null)
        
        public val sharedFlowNativeReplayCache: List<String>
          get() = sharedFlow.replayCache
    """.trimIndent())

    /**
     * We can't test this since [Throws] is a typealias in Kotlin/JVM
     * which is where our KSP tests are currently running.
     */
    @Test
    @Ignore
    fun throwsAnnotationStateFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        @get:Throws
        @NativeCoroutines
        val stateFlow: StateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val stateFlowNative: NativeFlow<String>
          get() = stateFlow.asNativeFlow(null)
        
        public val stateFlowNativeValue: String
          get() = stateFlow.value
    """.trimIndent())
}
