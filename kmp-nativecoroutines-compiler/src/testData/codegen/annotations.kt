@file:Suppress("OPTIONAL_DECLARATION_USAGE_IN_NON_COMMON_SOURCE")

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.nativecoroutines.runBoxTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

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

@NativeCoroutines
@OptIn(ExperimentalObjCName::class)
@ObjCName("objCNameFunction1ObjC")
suspend fun objCNameFunction1(): String = "OK8"

@NativeCoroutines
@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "objCNameFunction2Swift")
suspend fun objCNameFunction2(): String = "OK9"

@NativeCoroutines
@OptIn(ExperimentalObjCName::class)
@ObjCName("objCNameFunction3ObjC", "objCNameFunction3Swift")
suspend fun objCNameFunction3(): String = "OK10"

@NativeCoroutines
@OptIn(ExperimentalObjCName::class)
@ObjCName("objCNameProperty1ObjC")
val objCNameProperty1: StateFlow<String> = MutableStateFlow("OK11")

@NativeCoroutinesState
@OptIn(ExperimentalObjCName::class)
@ObjCName("objCNameProperty2ObjC")
val objCNameProperty2: StateFlow<String> = MutableStateFlow("OK12")

@NativeCoroutines
@OptIn(ExperimentalObjCName::class)
suspend fun objCNameFunctionParameter(@ObjCName("valueObjC") value: String): String = value

fun box() = runBoxTest {
    await(deprecatedFunction1Native())
    await(deprecatedFunction2Native())
    collect(deprecatedProperty1Native)
    collect(deprecatedProperty2Native)
    collect(deprecatedProperty4Native, maxValues = 1)
    value(deprecatedProperty4Value)
    await(objCNameFunction1Native())
    await(objCNameFunction2Native())
    await(objCNameFunction3Native())
    collect(objCNameProperty1Native, maxValues = 1)
    value(objCNameProperty1Value)
    collect(objCNameProperty2Flow, maxValues = 1)
    value(objCNameProperty2Value)
    await(objCNameFunctionParameterNative("OK13"))
}
