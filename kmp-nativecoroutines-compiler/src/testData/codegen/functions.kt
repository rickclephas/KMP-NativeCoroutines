import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.runBoxTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow

@NativeCoroutines
suspend fun returnSuspendValue(): String = "OK1"

@NativeCoroutines
suspend fun returnNullableSuspendValue(): String? = null

@NativeCoroutines
fun returnFlowValue(): Flow<String> = flowOf("OK3")

@NativeCoroutines
fun returnNullableFlowValue(): Flow<String?> = flowOf(null)

@NativeCoroutines
fun returnNullableFlow(): Flow<String>? = null

@NativeCoroutines
fun returnNullableFlowAndValue(): Flow<String?>? = null

@NativeCoroutines
fun returnStateFlowValue(): StateFlow<String> = MutableStateFlow("OK7")

class MyClass8 {
    @NativeCoroutines
    suspend fun returnSuspendValue(): String = "OK8"
}

@NativeCoroutines
suspend fun returnSuspendParameterValue(value: String): String = value

@NativeCoroutines
suspend fun returnSuspendParameterValue(value: Int): Int = value

@NativeCoroutines
suspend fun returnThrowsSuspendValue(): String = "OK10"

@NativeCoroutines
suspend fun returnSuspendVarargValue(vararg values: String): String = values[0]

class MyClass14<T>(private val value: T) {
    @NativeCoroutines
    suspend fun returnGenericSuspendValue(): T = value
}

@NativeCoroutinesRefined
suspend fun returnRefinedSuspendValue(): String = "OK13"

@NativeCoroutinesRefined
suspend fun returnSuspendFlowValue(): Flow<String> = flowOf("OK14")

@NativeCoroutines
suspend fun <T> returnGenericSuspendValue(value: T): T = value

class MyClass16 {
    @NativeCoroutines
    suspend fun <T1, T2 : T1> functionWithGenericValues(value1: T1, value2: T2): String =
        value1.toString() + value2.toString()
}

@NativeCoroutines
suspend inline fun <reified T> returnInlineSuspendValue(value: T): T = value

@NativeCoroutines
suspend fun returnNullableSuspendFlow(): Flow<String>? = null

@NativeCoroutines
suspend fun String.returnExtensionValue(): String = this

class MyClass20 {
    @NativeCoroutines
    suspend fun String.returnClassExtensionValue(): String = this
}

class MyClass21<out T> {
    @NativeCoroutines
    suspend fun returnGenericValue(): T? = null
}

interface MyInterface22 {
    @NativeCoroutines
    suspend fun returnInterfaceSuspendValue(): String
}

class MyClass22: MyInterface22 {
    @NativeCoroutines
    override suspend fun returnInterfaceSuspendValue(): String = "OK22"
}

class MyFlow23<T1, T2>(
    value1: T1,
    value2: T2,
): Flow<T2> by flowOf<T2>(value2)

@NativeCoroutines
fun returnCustomFlowValue(): MyFlow23<Int, String> = MyFlow23(23, "OK23")

fun box() = runBoxTest {
    await<String> { returnSuspendValueNative() }
    await<String?> { returnNullableSuspendValueNative() }
    collect(returnFlowValueNative())
    collect(returnNullableFlowValueNative())
    value(returnNullableFlowNative())
    value(returnNullableFlowAndValueNative())
    collect(returnStateFlowValueNative(), maxValues = 1)
    await<String> { MyClass8().returnSuspendValueNative() }
    await<String> { returnSuspendParameterValueNative("OK9") }
    await<Int> { returnSuspendParameterValueNative(9) }
    await<String> { returnThrowsSuspendValueNative() }
    await<String> { returnSuspendVarargValueNative("OK11") }
    await<String> { MyClass14("OK12").returnGenericSuspendValueNative() }
    await<String> { returnRefinedSuspendValueNative() }
    awaitAndCollect<String> { returnSuspendFlowValueNative() }
    await<String> { returnGenericSuspendValueNative("OK15") }
    await<String> { MyClass16().functionWithGenericValuesNative<CharSequence, String>("OK", "16") }
    await<String> { returnInlineSuspendValueNative("OK17") }
    awaitAndCollectNull<String> { returnNullableSuspendFlowNative() }
    await<String> { "OK19".returnExtensionValueNative() }
    with(MyClass20()) {
        await<String> { "OK20".returnClassExtensionValueNative() }
    }
    await<String?> { MyClass21<String>().returnGenericValueNative() }
    await<String> { MyClass22().returnInterfaceSuspendValueNative() }
    collect(returnCustomFlowValueNative())
}
