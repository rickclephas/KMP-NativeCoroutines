// FIR_IDENTICAL
// DIAGNOSTICS: -NOT_A_MULTIPLATFORM_COMPILATION -EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE
// EXPOSED_SEVERITY: WARNING
// K2_MODE

// FILE: customFlows.kt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CustomFlow<out T>: Flow<T>

interface CustomStateFlow<out T>: StateFlow<T>

// FILE: test.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.native.ShouldRefineInSwift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutines
suspend fun topLevelSuspendFunction(): Int = 0

@NativeCoroutinesRefined
fun topLevelFlowFunction(): Flow<Int> = throw Throwable()

@NativeCoroutines
fun topLevelSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

@NativeCoroutines
fun topLevelStateFlowFunction(): StateFlow<Int> = throw Throwable()

@NativeCoroutines
fun topLevelCustomFlowFunction(): CustomFlow<Int> = throw Throwable()

@NativeCoroutinesIgnore
suspend fun topLevelSuspendFlowFunction(): Flow<Int> = throw Throwable()

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
suspend fun topLevelRefinedSuspendFunction(): Int = 0

@NativeCoroutines
fun topLevelSuspendTypeFunction(): (suspend () -> Int) = throw Throwable()

@NativeCoroutines
fun topLevelSuspendTypeArgFunction(block: suspend () -> Int): Int = 0

@NativeCoroutines
fun (suspend () -> Int).topLevelSuspendTypeExtensionFunction(): Int = 0

@NativeCoroutines
fun topLevelFlowArgFunction(flow: Flow<Int>): Int = 0

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun topLevelSharedFlowArgFunction(flow: SharedFlow<Int>): Int = 0

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun topLevelStateFlowArgFunction(flow: StateFlow<Int>): Int = 0

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun topLevelCustomFlowArgFunction(flow: CustomFlow<Int>): Int = 0

@NativeCoroutines
fun Flow<Int>.topLevelFlowExtensionFunction(): Int = 0

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun SharedFlow<Int>.topLevelSharedFlowExtensionFunction(): Int = 0

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun StateFlow<Int>.topLevelStateFlowExtensionFunction(): Int = 0

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun CustomFlow<Int>.topLevelCustomFlowExtensionFunction(): Int = 0

@NativeCoroutines
fun topLevelFlowBlockFunction(block: () -> Flow<Int>): Int = 0

@NativeCoroutines
fun topLevelFlowArgBlockFunction(block: (Flow<Int>) -> Int): Int = 0

@NativeCoroutines
fun topLevelFlowExtensionBlockFunction(block: Flow<Int>.() -> Int): Int = 0

@NativeCoroutines
val topLevelFlowProperty: Flow<Int> get() = throw Throwable()

@NativeCoroutines
val topLevelSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

@NativeCoroutinesState
val topLevelStateFlowProperty: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesIgnore
val topLevelCustomFlowProperty: CustomFlow<Int> get() = throw Throwable()

@NativeCoroutinesState
val topLevelCustomStateFlowProperty: CustomStateFlow<Int> get() = throw Throwable()

@OptIn(ExperimentalObjCRefinement::class)
@ShouldRefineInSwift
val topLevelRefinedFlowProperty: Flow<Int> get() = throw Throwable()

interface TestInterface {

    @NativeCoroutines
    suspend fun suspendInterfaceFunction(): Int

    @NativeCoroutines
    val flowInterfaceProperty: Flow<Int>
}

class TestClassA: TestInterface {

    override suspend fun suspendInterfaceFunction(): Int = 0

    override val flowInterfaceProperty: Flow<Int> get() = throw Throwable()

    @NativeCoroutines
    protected suspend fun protectedSuspendFunction(): Int = 0

    @NativeCoroutines
    protected val protectedFlowProperty: Flow<Int> get() = throw Throwable()

    @NativeCoroutinesRefinedState
    protected val protectedStateFlowProperty: StateFlow<Int> get() = throw Throwable()

    @NativeCoroutinesIgnore
    fun implicitFlowFunction() = flowInterfaceProperty

    private suspend fun privateSuspendFunction(): Int = 0

    private val privateFlowProperty: Flow<Int> get() = throw Throwable()
}

internal class TestClassB {

    suspend fun suspendFunction(): Int = 0

    val flowProperty: Flow<Int> get() = throw Throwable()
}

expect class TestClassC {
    @NativeCoroutines
    suspend fun suspendFunction(): Int

    @NativeCoroutines
    val flowProperty: Flow<Int>
}

actual class TestClassC {
    actual suspend fun suspendFunction(): Int = 0

    actual val flowProperty: Flow<Int> get() = throw Throwable()
}
