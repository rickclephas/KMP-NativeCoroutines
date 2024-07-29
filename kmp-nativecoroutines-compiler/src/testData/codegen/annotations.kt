import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.runBoxTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

@NativeCoroutines
@Deprecated("This is deprecated 1")
suspend fun deprecatedFunction1(): String = "OK1"

@NativeCoroutines
@Deprecated("This is deprecated 2", level = DeprecationLevel.WARNING)
suspend fun deprecatedFunction2(): String = "OK2"

@NativeCoroutines
@Deprecated("This is deprecated 3", ReplaceWith("deprecatedFunction2()"), DeprecationLevel.ERROR)
suspend fun deprecatedFunction3(): String = "OK3"

@NativeCoroutines
@Deprecated("This is deprecated 4")
val deprecatedProperty1: Flow<String> = flowOf("OK4")

@NativeCoroutines
@Deprecated("This is deprecated 5", level = DeprecationLevel.WARNING)
val deprecatedProperty2: Flow<String> = flowOf("OK5")

@NativeCoroutines
@Deprecated("This is deprecated 6", ReplaceWith("deprecatedProperty2"), DeprecationLevel.ERROR)
val deprecatedProperty3: Flow<String> = flowOf("OK6")

@NativeCoroutines
@get:Deprecated("This is deprecated 7")
val deprecatedProperty4: MutableStateFlow<String> = MutableStateFlow("OK7")

fun box() = runBoxTest {
    await(deprecatedFunction1Native())
    await(deprecatedFunction2Native())
    collect(deprecatedProperty1Native)
    collect(deprecatedProperty2Native)
    collect(deprecatedProperty4Native, maxValues = 1)
}
