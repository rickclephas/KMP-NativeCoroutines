package com.rickclephas.kmp.nativecoroutines.ksp

import org.junit.Test

class CoroutineScopeProviderTests: CompilationTests() {

    @Test
    fun fileScopeSuspendFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        
        @NativeCoroutineScope
        internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        
        @NativeCoroutines
        suspend fun returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend(coroutineScope) {
            returnSuspendValue() }
    """.trimIndent())

    @Test
    fun fileScopeFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutineScope
        internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        
        @NativeCoroutines
        fun returnFlowValue(): Flow<String> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public fun returnFlowValueNative(): NativeFlow<String> =
            returnFlowValue().asNativeFlow(coroutineScope)
    """.trimIndent())

    @Test
    fun fileScopeSuspendFlowFunction() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutineScope
        internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        
        @NativeCoroutines
        suspend fun returnSuspendFlowValue(): Flow<String> = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        
        public fun returnSuspendFlowValueNative(): NativeSuspend<NativeFlow<String>> =
            nativeSuspend(coroutineScope) { returnSuspendFlowValue().asNativeFlow(coroutineScope) }
    """.trimIndent())

    @Test
    fun fileScopeFlowProperty() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.flow.Flow
        
        @NativeCoroutineScope
        internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        
        @NativeCoroutines
        val globalFlow: Flow<String> get() = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeFlow
        import com.rickclephas.kmp.nativecoroutines.asNativeFlow
        import kotlin.String
        
        public val globalFlowNative: NativeFlow<String>
          get() = globalFlow.asNativeFlow(coroutineScope)
    """.trimIndent())

    @Test
    fun classScope() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        
        class MyClass {
            @NativeCoroutineScope
            internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        
            @NativeCoroutines
            suspend fun returnSuspendValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend(coroutineScope)
            { returnSuspendValue() }
    """.trimIndent())

    @Test
    fun superClassScope() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        
        open class SuperClass {
            @NativeCoroutineScope
            internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        }
        
        class MyClass: SuperClass() {
            @NativeCoroutines
            suspend fun returnSuspendValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend(coroutineScope)
            { returnSuspendValue() }
    """.trimIndent())

    @Test
    fun subClassScope() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        
        open class SuperClass {
            @NativeCoroutineScope
            internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        }
        
        class MyClass: SuperClass() {
            @NativeCoroutineScope
            internal val myCoroutineScope = CoroutineScope(Dispatchers.Default)
            @NativeCoroutines
            suspend fun returnSuspendValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> =
            nativeSuspend(myCoroutineScope) { returnSuspendValue() }
    """.trimIndent())

    @Test
    fun receiverClassScope() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        
        class MyClass {
            @NativeCoroutineScope
            internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        }
        
        @NativeCoroutines
        suspend fun MyClass.returnSuspendValue(): String = TODO()
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend(coroutineScope)
            { returnSuspendValue() }
    """.trimIndent())

    @Test
    fun withoutContainedClassScope() = runKspTest("""
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
        import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        
        class MyOtherClass {
            @NativeCoroutineScope
            internal val coroutineScope = CoroutineScope(Dispatchers.Default)
        }
        
        class MyClass {
            @NativeCoroutines
            suspend fun MyOtherClass.returnSuspendValue(): String = TODO()
        }
    """.trimIndent(), """
        import com.rickclephas.kmp.nativecoroutines.NativeSuspend
        import com.rickclephas.kmp.nativecoroutines.nativeSuspend
        import kotlin.String
        import kotlin.run
        
        public fun MyClass.returnSuspendValueNative(`receiver`: MyOtherClass): NativeSuspend<String> =
            nativeSuspend(null) { run { `receiver`.returnSuspendValue() } }
    """.trimIndent())
}
