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

@Throws
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

fun box() = runBoxTest {
    await(returnSuspendValueNative())
    await(returnNullableSuspendValueNative())
    collect(returnFlowValueNative())
    collect(returnNullableFlowValueNative())
    value(returnNullableFlowNative())
    value(returnNullableFlowAndValueNative())
    collect(returnStateFlowValueNative(), maxValues = 1)
    await(MyClass8().returnSuspendValueNative())
    await(returnSuspendParameterValueNative("OK9"))
    await(returnSuspendParameterValueNative(9))
    await(returnThrowsSuspendValueNative())
    await(returnSuspendVarargValueNative("OK11"))
    await(MyClass14("OK12").returnGenericSuspendValueNative())
    await(returnRefinedSuspendValueNative())
    awaitAndCollect(returnSuspendFlowValueNative())
    await(returnGenericSuspendValueNative("OK15"))
    await(MyClass16().functionWithGenericValuesNative<CharSequence, String>("OK", "16"))
}
