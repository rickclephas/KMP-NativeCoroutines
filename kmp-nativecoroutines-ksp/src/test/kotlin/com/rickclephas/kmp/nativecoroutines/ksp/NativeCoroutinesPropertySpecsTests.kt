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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "globalFlow")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "globalSharedFlow")
        public val globalSharedFlowNative: NativeFlow<String>
          get() = globalSharedFlow.asNativeFlow(null)
        
        public val globalSharedFlowReplayCache: List<String>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "globalStateFlow")
        public val globalStateFlowNative: NativeFlow<String>
          get() = globalStateFlow.asNativeFlow(null)
        
        public val globalStateFlowValue: String
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "globalMutableStateFlow")
        public val globalMutableStateFlowNative: NativeFlow<String>
          get() = globalMutableStateFlow.asNativeFlow(null)
        
        public var globalMutableStateFlowValue: String
          get() = globalMutableStateFlow.value
          set(`value`) {
            globalMutableStateFlow.value = value
          }
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "globalCustomFlow")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableCustomFlow")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableCustomFlowValue")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableFlowValue")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableSharedFlowValue")
        public val nullableSharedFlowValueNative: NativeFlow<String?>
          get() = nullableSharedFlowValue.asNativeFlow(null)
        
        public val nullableSharedFlowValueReplayCache: List<String?>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableStateFlowValue")
        public val nullableStateFlowValueNative: NativeFlow<String?>
          get() = nullableStateFlowValue.asNativeFlow(null)
        
        public val nullableStateFlowValueValue: String?
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableFlow")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableSharedFlow")
        public val nullableSharedFlowNative: NativeFlow<String>?
          get() = nullableSharedFlow?.asNativeFlow(null)
        
        public val nullableSharedFlowReplayCache: List<String>?
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableStateFlow")
        public val nullableStateFlowNative: NativeFlow<String>?
          get() = nullableStateFlow?.asNativeFlow(null)
        
        public val nullableStateFlowValue: String?
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableFlowAndValue")
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableSharedFlowAndValue")
        public val nullableSharedFlowAndValueNative: NativeFlow<String?>?
          get() = nullableSharedFlowAndValue?.asNativeFlow(null)
        
        public val nullableSharedFlowAndValueReplayCache: List<String?>?
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "nullableStateFlowAndValue")
        public val nullableStateFlowAndValueNative: NativeFlow<String?>?
          get() = nullableStateFlowAndValue?.asNativeFlow(null)
        
        public val nullableStateFlowAndValueValue: String?
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "genericSharedFlow")
        public val <T> MyClass<T>.genericSharedFlowNative: NativeFlow<T>
          get() = genericSharedFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericSharedFlowReplayCache: List<T>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "genericStateFlow")
        public val <T> MyClass<T>.genericStateFlowNative: NativeFlow<T>
          get() = genericStateFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericStateFlowValue: T
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "genericSharedFlow")
        public val <T> MyClass<T>.genericSharedFlowNative: NativeFlow<T>
          get() = genericSharedFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericSharedFlowReplayCache: List<T>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "genericStateFlow")
        public val <T> MyClass<T>.genericStateFlowNative: NativeFlow<T>
          get() = genericStateFlow.asNativeFlow(null)
        
        public val <T> MyClass<T>.genericStateFlowValue: T
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
        import kotlin.native.ObjCName
        
        /**
         * KDoc for [kdocSharedFlow]
         */
        @ObjCName(name = "kdocSharedFlow")
        public val kdocSharedFlowNative: NativeFlow<String>
          get() = kdocSharedFlow.asNativeFlow(null)
        
        /**
         * KDoc for [kdocSharedFlow]
         */
        public val kdocSharedFlowReplayCache: List<String>
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
        import kotlin.native.ObjCName
        
        /**
         * KDoc for [kdocStateFlow]
         */
        @ObjCName(name = "kdocStateFlow")
        public val kdocStateFlowNative: NativeFlow<String>
          get() = kdocStateFlow.asNativeFlow(null)
        
        /**
         * KDoc for [kdocStateFlow]
         */
        public val kdocStateFlowValue: String
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "sharedFlow")
        public val MyClass.sharedFlowNative: NativeFlow<String>
          get() = sharedFlow.asNativeFlow(null)
        
        public val MyClass.sharedFlowReplayCache: List<String>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "stateFlow")
        public val MyClass.stateFlowNative: NativeFlow<String>
          get() = stateFlow.asNativeFlow(null)
        
        public val MyClass.stateFlowValue: String
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "sharedFlow")
        public val String.sharedFlowNative: NativeFlow<String>
          get() = sharedFlow.asNativeFlow(null)
        
        public val String.sharedFlowReplayCache: List<String>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "stateFlow")
        public val String.stateFlowNative: NativeFlow<String>
          get() = stateFlow.asNativeFlow(null)
        
        public val String.stateFlowValue: String
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "myFlow")
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
        import kotlin.native.ObjCName
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        @ObjCName(name = "sharedFlow")
        public val sharedFlowNative: NativeFlow<String>
          @get:ExperimentalStdlibApi
          get() = sharedFlow.asNativeFlow(null)
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        public val sharedFlowReplayCache: List<String>
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
        import kotlin.native.ObjCName
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        @ObjCName(name = "stateFlow")
        public val stateFlowNative: NativeFlow<String>
          @get:ExperimentalStdlibApi
          get() = stateFlow.asNativeFlow(null)
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = "", imports = arrayOf()),
          level = DeprecationLevel.WARNING,
        )
        public val stateFlowValue: String
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "sharedFlow")
        public val sharedFlowNative: NativeFlow<String>
          get() = sharedFlow.asNativeFlow(null)
        
        public val sharedFlowReplayCache: List<String>
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "stateFlow")
        public val stateFlowNative: NativeFlow<String>
          get() = stateFlow.asNativeFlow(null)
        
        public val stateFlowValue: String
          get() = stateFlow.value
    """.trimIndent())

    @Test
    fun globalStateProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
        import kotlinx.coroutines.flow.StateFlow
        
        @NativeCoroutinesState
        val globalState: StateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        public val globalStateFlow: NativeFlow<String>
          get() = globalState.asNativeFlow(null)
        
        @ObjCName(name = "globalState")
        public val globalStateValue: String
          get() = globalState.value
    """.trimIndent())

    @Test
    fun globalMutableStateProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
        import kotlinx.coroutines.flow.MutableStateFlow
        
        @NativeCoroutinesState
        val globalMutableState: MutableStateFlow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        public val globalMutableStateFlow: NativeFlow<String>
          get() = globalMutableState.asNativeFlow(null)
        
        @ObjCName(name = "globalMutableState")
        public var globalMutableStateValue: String
          get() = globalMutableState.value
          set(`value`) {
            globalMutableState.value = value
          }
    """.trimIndent())

    @Test
    fun genericClassWithVariance() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        class GenericClass<out T> {
            @NativeCoroutines
            val flow: Flow<T> get() = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.native.ObjCName
        
        @ObjCName(name = "flow")
        public val <T> GenericClass<T>.flowNative: NativeFlow<T>
          get() = flow.asNativeFlow(null)
    """.trimIndent())

    @Test
    fun optInAnnotatedProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @OptIn(ExperimentalStdlibApi::class)
        @NativeCoroutines
        val flow: Flow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.ExperimentalStdlibApi
        import kotlin.OptIn
        import kotlin.String
        import kotlin.native.ObjCName
        
        @OptIn(ExperimentalStdlibApi::class)
        @ObjCName(name = "flow")
        public val flowNative: NativeFlow<String>
          get() = flow.asNativeFlow(null)
    """.trimIndent())

    @Test
    fun multiOptInAnnotatedProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @RequiresOptIn
        annotation class A
        
        @RequiresOptIn
        annotation class B
        
        @OptIn(A::class, B::class)
        @NativeCoroutines
        val flow: Flow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.OptIn
        import kotlin.String
        import kotlin.native.ObjCName
        
        @OptIn(
          A::class,
          B::class,
        )
        @ObjCName(name = "flow")
        public val flowNative: NativeFlow<String>
          get() = flow.asNativeFlow(null)
    """.trimIndent())
}
