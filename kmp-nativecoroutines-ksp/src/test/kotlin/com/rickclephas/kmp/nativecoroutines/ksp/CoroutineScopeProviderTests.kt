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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun returnSuspendValueNative(): NativeSuspend<String> = nativeSuspend<String>(coroutineScope)
            { returnSuspendValue() }
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnFlowValue")
        public fun returnFlowValueNative(): NativeFlow<String> =
            returnFlowValue().asNativeFlow<String>(coroutineScope)
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendFlowValue")
        public fun returnSuspendFlowValueNative(): NativeSuspend<NativeFlow<String>> =
            nativeSuspend<NativeFlow<String>>(coroutineScope) {
            returnSuspendFlowValue().asNativeFlow<String>(coroutineScope) }
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "globalFlow")
        public val globalFlowNative: NativeFlow<String>
          get() = globalFlow.asNativeFlow<String>(coroutineScope)
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> =
            nativeSuspend<String>(coroutineScope) { returnSuspendValue() }
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> =
            nativeSuspend<String>(coroutineScope) { returnSuspendValue() }
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> =
            nativeSuspend<String>(myCoroutineScope) { returnSuspendValue() }
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
        import kotlin.native.ObjCName
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyClass.returnSuspendValueNative(): NativeSuspend<String> =
            nativeSuspend<String>(coroutineScope) { returnSuspendValue() }
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
        import kotlin.native.ObjCName
        import kotlin.run
        
        @ObjCName(name = "returnSuspendValue")
        public fun MyClass.returnSuspendValueNative(@ObjCName(swiftName = "_") `receiver`: MyOtherClass):
            NativeSuspend<String> = nativeSuspend<String>(null) { run { `receiver`.returnSuspendValue() } }
    """.trimIndent())
}
