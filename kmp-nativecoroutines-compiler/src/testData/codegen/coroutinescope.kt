// FILE: coroutinescope1.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@NativeCoroutineScope
internal val coroutineScope1 = CoroutineScope(Dispatchers.Default)

@NativeCoroutines
suspend fun returnSuspendValue(): String = "OK1"

@NativeCoroutines
val flowProperty: Flow<String> = flowOf("OK2")

open class MyClass1 {
    @NativeCoroutineScope
    internal val coroutineScope2 = CoroutineScope(Dispatchers.Default)

    @NativeCoroutines
    suspend fun returnSuspendValue(): String = "OK3"
}

class MyClass2: MyClass1() {
    @NativeCoroutines
    val flowProperty: Flow<String> = flowOf("OK4")
}

class MyClass3: MyClass1() {
    @NativeCoroutineScope
    internal val coroutineScope3 = CoroutineScope(Dispatchers.Default)

    @NativeCoroutines
    suspend fun returnOtherSuspendValue(): String = "OK5"
}

@NativeCoroutines
val MyClass3.flowExtProperty1: Flow<String> get() = flowOf("OK6")

class MyClass4 {
    @NativeCoroutines
    suspend fun MyClass1.returnExtSuspendValue(): String = "OK7"
}

// FILE: coroutinescope2.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.runBoxTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@NativeCoroutines
val MyClass3.flowExtProperty2: Flow<String> get() = flowOf("OK8")

class MyClass5 {
    @NativeCoroutines
    suspend fun MyClass1.returnExtSuspendValue(): String = "OK9"
}

fun box() = runBoxTest {
    await(returnSuspendValueNative())
    collect(flowPropertyNative)
    await(MyClass1().returnSuspendValueNative())
    collect(MyClass2().flowPropertyNative)
    await(MyClass3().returnOtherSuspendValueNative())
    collect(MyClass3().flowExtProperty1Native)
    with(MyClass4()) {
        await(MyClass1().returnExtSuspendValueNative())
    }
    collect(MyClass3().flowExtProperty2Native)
    with(MyClass5()) {
        await(MyClass1().returnExtSuspendValueNative())
    }
}
