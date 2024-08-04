import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.nativecoroutines.runBoxTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.collections.listOf

@NativeCoroutines
val topLevelFlow: Flow<String> = flowOf("OK1")

@NativeCoroutines
val topLevelSharedFlow: SharedFlow<String> = MutableSharedFlow<String>(1).apply { tryEmit("OK2") }

@NativeCoroutines
val topLevelStateFlow: StateFlow<String> = MutableStateFlow("OK3")

@NativeCoroutines
val topLevelMutableStateFlow: MutableStateFlow<String> = MutableStateFlow("OK4")

@NativeCoroutines
val nullableFlowValue: Flow<String?> = flowOf(null)

@NativeCoroutines
val nullableSharedFlowValue: SharedFlow<String?> = MutableSharedFlow<String?>(1).apply { tryEmit(null) }

@NativeCoroutines
val nullableStateFlowValue: StateFlow<String?> = MutableStateFlow(null)

@NativeCoroutines
val nullableFlow: Flow<String>? get() = null

@NativeCoroutines
val nullableSharedFlow: SharedFlow<String>? get() = null

@NativeCoroutines
val nullableStateFlowProperty: StateFlow<String>? get() = null

@NativeCoroutines
val nullableFlowAndValue: Flow<String?>? get() = null

@NativeCoroutines
val nullableSharedFlowAndValue: SharedFlow<String?>? get() = null

@NativeCoroutines
val nullableStateFlowAndValue: StateFlow<String?>? get() = null

data class MyGenericClass1<T>(val value: T)

@NativeCoroutines
val <T> MyGenericClass1<T>.genericFlow: Flow<T> get() = flowOf(value)

@NativeCoroutines
val <T> MyGenericClass1<T>.genericSharedFlow: SharedFlow<T> get() = MutableSharedFlow<T>(1).apply { tryEmit(value) }

@NativeCoroutines
val <T> MyGenericClass1<T>.genericStateFlow: StateFlow<T> get() = MutableStateFlow(value)

class MyGenericClass2<T>(private val value: T) {

    @NativeCoroutines
    val genericFlow: Flow<T> = flowOf(value)

    @NativeCoroutines
    val genericSharedFlow: SharedFlow<T> = MutableSharedFlow<T>(1).apply { tryEmit(value) }

    @NativeCoroutines
    val genericStateFlow: StateFlow<T> = MutableStateFlow(value)
}

@NativeCoroutines
val String.extensionFlow: Flow<String> get() = flowOf(this)

@NativeCoroutines
val String.extensionSharedFlow: SharedFlow<String> get() = MutableSharedFlow<String>(1).apply { tryEmit(this@extensionSharedFlow) }

@NativeCoroutines
val String.extensionStateFlow: StateFlow<String> get() = MutableStateFlow(this)

@NativeCoroutinesState
val stateProperty: StateFlow<String> = MutableStateFlow("OK23")

@NativeCoroutinesState
val mutableStateProperty: MutableStateFlow<String> = MutableStateFlow("OK24")

@NativeCoroutinesRefined
val refinedFlow: Flow<String> = flowOf("OK25")

@NativeCoroutinesRefinedState
val refinedState: StateFlow<String> = MutableStateFlow("OK26")

@NativeCoroutinesState
val mutableNullableStateProperty: MutableStateFlow<String>? = MutableStateFlow("OK27")

interface MyInterface28 {
    @NativeCoroutines
    val interfaceFlowValue: Flow<String>
}

class MyClass28: MyInterface28 {
    @NativeCoroutines
    override val interfaceFlowValue: Flow<String> = flowOf("OK28")
}

class MyFlow29<T1, T2>(
    value1: T1,
    value2: T2,
): Flow<T2?> by flowOf<T2?>(null)

@NativeCoroutines
val customFlowValue: MyFlow29<Int, String> get() = MyFlow29(29, "OK29")

class MyClass30 {
    @NativeCoroutines
    val unitFlowValue: Flow<Unit> get() = flowOf(Unit)
}

fun box() = runBoxTest {
    collect(topLevelFlowNative)
    collect(topLevelSharedFlowNative, maxValues = 1)
    values(topLevelSharedFlowReplayCache)
    collect(topLevelStateFlowNative, maxValues = 1)
    value(topLevelStateFlowValue)
    collect(topLevelMutableStateFlowNative, maxValues = 1)
    value(topLevelMutableStateFlowValue)
    collect(nullableFlowValueNative)
    collect(nullableSharedFlowValueNative, maxValues = 1)
    values(nullableSharedFlowValueReplayCache)
    collect(nullableStateFlowValueNative, maxValues = 1)
    value(nullableStateFlowValueValue)
    value(nullableFlowNative)
    value(nullableSharedFlowNative)
    value(nullableSharedFlowReplayCache)
    value(nullableStateFlowPropertyNative)
    value(nullableStateFlowPropertyValue)
    value(nullableFlowAndValueNative)
    value(nullableSharedFlowAndValueNative)
    value(nullableSharedFlowAndValueReplayCache)
    value(nullableStateFlowAndValueNative)
    value(nullableStateFlowAndValueValue)
    collect(MyGenericClass1("OK14").genericFlowNative)
    collect(MyGenericClass1("OK15").genericSharedFlowNative, maxValues = 1)
    values(MyGenericClass1("OK15").genericSharedFlowReplayCache)
    collect(MyGenericClass1("OK16").genericStateFlowNative, maxValues = 1)
    value(MyGenericClass1("OK16").genericStateFlowValue)
    collect(MyGenericClass2("OK17").genericFlowNative)
    collect(MyGenericClass2("OK18").genericSharedFlowNative, maxValues = 1)
    values(MyGenericClass2("OK18").genericSharedFlowReplayCache)
    collect(MyGenericClass2("OK19").genericStateFlowNative, maxValues = 1)
    value(MyGenericClass2("OK19").genericStateFlowValue)
    collect("OK20".extensionFlowNative)
    collect("OK21".extensionSharedFlowNative, maxValues = 1)
    values("OK21".extensionSharedFlowReplayCache)
    collect("OK22".extensionStateFlowNative, maxValues = 1)
    value("OK22".extensionStateFlowValue)
    value(statePropertyValue)
    collect(statePropertyFlow, maxValues = 1)
    value(mutableStatePropertyValue)
    collect(mutableStatePropertyFlow, maxValues = 1)
    collect(refinedFlowNative)
    value(refinedStateValue)
    collect(refinedStateFlow, maxValues = 1)
    value(mutableNullableStatePropertyValue)
    collect(mutableNullableStatePropertyFlow!!, maxValues = 1)
    collect(MyClass28().interfaceFlowValueNative)
    collect(customFlowValueNative)
    collect(MyClass30().unitFlowValueNative)
}
