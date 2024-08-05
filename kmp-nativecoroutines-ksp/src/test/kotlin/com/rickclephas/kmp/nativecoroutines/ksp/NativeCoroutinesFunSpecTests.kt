package com.rickclephas.kmp.nativecoroutines.ksp

import org.junit.Ignore
import org.junit.Test

class NativeCoroutinesFunSpecTests: CompilationTests() {

    @Test
    fun suspendFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun nullableSuspendFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun returnNullableSuspendValue(): String? = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnNullableSuspendValue")
        public fun returnNullableSuspendValueNative(): NativeSuspend<String?> = nativeSuspend<String?>(null)
            { returnNullableSuspendValue() }
    """.trimIndent())

    @Test
    fun flowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutines
        fun returnFlowValue(): Flow<String> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnFlowValue")
        public fun returnFlowValueNative(): NativeFlow<String> =
            returnFlowValue().asNativeFlow<String>(null)
    """.trimIndent())

    @Test
    fun customFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.AbstractFlow
        
        abstract class CustomFlow<T1, T2>: AbstractFlow<T2>()
        
        @NativeCoroutines
        fun <R> returnCustomFlowValue(): CustomFlow<String, R> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnCustomFlowValue")
        public fun <R> returnCustomFlowValueNative(): NativeFlow<R> =
            returnCustomFlowValue<R>().asNativeFlow<R>(null)
    """.trimIndent())

    @Test
    fun nullableFlowValueFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutines
        fun returnNullableFlowValue(): Flow<String?> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnNullableFlowValue")
        public fun returnNullableFlowValueNative(): NativeFlow<String?> =
            returnNullableFlowValue().asNativeFlow<String?>(null)
    """.trimIndent())

    @Test
    fun nullableFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutines
        fun returnNullableFlow(): Flow<String>? = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnNullableFlow")
        public fun returnNullableFlowNative(): NativeFlow<String>? =
            returnNullableFlow()?.asNativeFlow<String>(null)
    """.trimIndent())

    @Test
    fun nullableFlowAndValueFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutines
        fun returnNullableFlowAndValue(): Flow<String?>? = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnNullableFlowAndValue")
        public fun returnNullableFlowAndValueNative(): NativeFlow<String?>? =
            returnNullableFlowAndValue()?.asNativeFlow<String?>(null)
    """.trimIndent())

    @Test
    fun stateFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.StateFlow
        
        @NativeCoroutines
        fun returnStateFlowValue(): StateFlow<String> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnStateFlowValue")
        public fun returnStateFlowValueNative(): NativeFlow<String> =
            returnStateFlowValue().asNativeFlow<String>(null)
    """.trimIndent())

    @Test
    fun suspendFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutines
        suspend fun returnSuspendFlowValue(): Flow<String> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendFlowValue")
        public fun returnSuspendFlowValueNative(): NativeSuspend<NativeFlow<String>> =
            nativeSuspend<NativeFlow<String>>(null) { returnSuspendFlowValue().asNativeFlow<String>(null) }
    """.trimIndent())

    @Test
    fun suspendNullableFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutines
        suspend fun returnSuspendFlowValue(): Flow<String>? = null
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendFlowValue")
        public fun returnSuspendFlowValueNative(): NativeSuspend<NativeFlow<String>?> =
            nativeSuspend<NativeFlow<String>?>(null) { returnSuspendFlowValue()?.asNativeFlow<String>(null)
            }
    """.trimIndent())

    @Test
    fun genericFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun <T> returnGenericSuspendValue(): T = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnGenericSuspendValue")
        public fun <T> returnGenericSuspendValueNative(): NativeSuspend<T> = nativeSuspend<T>(null) {
            returnGenericSuspendValue<T>() }
    """.trimIndent())

    @Test
    fun genericClassFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        class MyClass<T> {
            @NativeCoroutines
            suspend fun returnClassGenericSuspendValue(): T = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnClassGenericSuspendValue")
        public fun <T> MyClass<T>.returnClassGenericSuspendValueNative(): NativeSuspend<T> =
            nativeSuspend<T>(null) { returnClassGenericSuspendValue() }
    """.trimIndent())

    @Test
    fun genericClassGenericFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        class MyClass<T> {
            @NativeCoroutines
            suspend fun <R> returnGenericSuspendValue(input: T): R = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnGenericSuspendValue")
        public fun <T, R> MyClass<T>.returnGenericSuspendValueNative(input: T): NativeSuspend<R> =
            nativeSuspend<R>(null) { returnGenericSuspendValue<R>(input) }
    """.trimIndent())

    @Test
    fun kdocFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        /**
         * KDoc for [returnSuspendValue]
         */
        @NativeCoroutines
        suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        /**
         * KDoc for [returnSuspendValue]
         */
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun protectedOpenClassFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        open class MyClass {
            @NativeCoroutines
            protected suspend fun returnSuspendValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun extensionFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun String.returnReceiverValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnReceiverValue")
        public fun String.returnReceiverValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnReceiverValue() }
    """.trimIndent())

    @Test
    fun classExtensionFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        class MyClass {
            @NativeCoroutines
            suspend fun String.returnReceiverValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        import kotlin.run
        
        @ObjCName(name = "returnReceiverValue")
        public fun MyClass.returnReceiverValueNative(@ObjCName(swiftName = "_") `receiver`: String):
            NativeSuspend<String> = nativeSuspend<String>(null) { run { `receiver`.returnReceiverValue() } }
    """.trimIndent())

    @Test
    fun parameterFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun returnSuspendValue(value: String): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(`value`: String): NativeSuspend<String> =
            nativeSuspend<String>(null) { returnSuspendValue(`value`) }
    """.trimIndent())

    @Test
    fun implicitTypeFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun returnSuspendValue(value: String) = value
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(`value`: String): NativeSuspend<String> =
            nativeSuspend<String>(null) { returnSuspendValue(`value`) }
    """.trimIndent())

    @Test
    fun implicitFlowTypeFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlinx.coroutines.flow.flow
        
        @NativeCoroutines
        fun returnFlowValue(value: String) = flow { emit(value) }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnFlowValue")
        public fun returnFlowValueNative(`value`: String): NativeFlow<String> =
            returnFlowValue(`value`).asNativeFlow<String>(null)
    """.trimIndent())

    @Test
    fun annotatedFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @Deprecated("it's old")
        @NativeCoroutines
        suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.Deprecated
        import kotlin.DeprecationLevel
        import kotlin.ReplaceWith
        import kotlin.String
        import kotlin.native.ObjCName
        
        @Deprecated(
          message = "it's old",
          replaceWith = ReplaceWith(expression = ""),
          level = DeprecationLevel.WARNING,
        )
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    /**
     * We can't test this since [Throws] is a typealias in Kotlin/JVM
     * which is where our KSP tests are currently running.
     */
    @Test
    @Ignore
    fun throwsAnnotation() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @Throws
        @NativeCoroutines
        suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun varargParameterFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun returnSuspendValue(vararg values: String): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(vararg values: String): NativeSuspend<String> =
            nativeSuspend<String>(null) { returnSuspendValue(*values) }
    """.trimIndent())

    @Test
    fun genericClassWithVariance() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        class GenericClass<out T> {
            @NativeCoroutines
            suspend fun returnGenericSuspendValue(): T = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnGenericSuspendValue")
        public fun <T> GenericClass<T>.returnGenericSuspendValueNative(): NativeSuspend<T> =
            nativeSuspend<T>(null) { returnGenericSuspendValue() }
    """.trimIndent())

    @Test
    fun refinedFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
        
        @NativeCoroutinesRefined
        suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        import kotlin.native.ShouldRefineInSwift
        
        @ObjCName(name = "returnSuspendValue")
        @ShouldRefineInSwift
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun gh124() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import kotlin.native.ObjCName
        
        @NativeCoroutines
        suspend fun getString(@ObjCName(swiftName = "_") index: Int): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.Int
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "getString")
        public fun getStringNative(@ObjCName(swiftName = "_") index: Int): NativeSuspend<String> =
            nativeSuspend<String>(null) { getString(index) }
    """.trimIndent())

    @Test
    fun overrideFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        interface MyInterface {
            @NativeCoroutines
            suspend fun returnSuspendValue(): String
        }
        
        class MyClass: MyInterface {
            @NativeCoroutines
            override suspend fun returnSuspendValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyInterface.returnSuspendValueNative(): NativeSuspend<String> =
            nativeSuspend<String>(null) { returnSuspendValue() }
    """.trimIndent())

    @Test
    fun actualFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        expect suspend fun returnSuspendValue(): String
        
        @NativeCoroutines
        actual suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(null) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun unitFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        
        @NativeCoroutines
        suspend fun returnSuspendUnit(): Unit = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.NativeUnit
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendUnit")
        public fun returnSuspendUnitNative(): NativeSuspend<NativeUnit?> = nativeSuspend(null) {
            returnSuspendUnit() }
    """.trimIndent())

}
