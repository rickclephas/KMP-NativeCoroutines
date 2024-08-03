// FIR_IDENTICAL
// DIAGNOSTICS: -NOT_A_MULTIPLATFORM_COMPILATION -EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE
// EXPOSED_SEVERITY: ERROR

// FILE: customFlows.kt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CustomFlow<out T>: Flow<T>

interface CustomStateFlow<out T>: StateFlow<T>

// FILE: test.kt

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.native.ShouldRefineInSwift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

<!EXPOSED_SUSPEND_FUNCTION_ERROR!>suspend<!> fun topLevelSuspendFunction(): Int = 0

fun topLevelFlowFunction(): <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!> = throw Throwable()

fun topLevelSharedFlowFunction(): <!EXPOSED_FLOW_TYPE_ERROR!>SharedFlow<Int><!> = throw Throwable()

fun topLevelStateFlowFunction(): <!EXPOSED_FLOW_TYPE_ERROR!>StateFlow<Int><!> = throw Throwable()

fun topLevelCustomFlowFunction(): <!EXPOSED_FLOW_TYPE_ERROR!>CustomFlow<Int><!> = throw Throwable()

<!EXPOSED_SUSPEND_FUNCTION_ERROR!>suspend<!> fun topLevelSuspendFlowFunction(): <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!> = throw Throwable()

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
suspend fun topLevelRefinedSuspendFunction(): Int = 0

fun topLevelSuspendTypeFunction(): <!EXPOSED_SUSPEND_TYPE_ERROR!>(suspend () -> Int)<!> = throw Throwable()

fun topLevelSuspendTypeArgFunction(block: <!EXPOSED_SUSPEND_TYPE_ERROR!>suspend () -> Int<!>): Int = 0

fun <!EXPOSED_SUSPEND_TYPE_ERROR!>(suspend () -> Int)<!>.topLevelSuspendTypeExtensionFunction(): Int = 0

fun topLevelFlowArgFunction(flow: <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!>): Int = 0

fun topLevelSharedFlowArgFunction(flow: <!EXPOSED_FLOW_TYPE_ERROR!>SharedFlow<Int><!>): Int = 0

fun topLevelStateFlowArgFunction(flow: <!EXPOSED_FLOW_TYPE_ERROR!>StateFlow<Int><!>): Int = 0

fun topLevelCustomFlowArgFunction(flow: <!EXPOSED_FLOW_TYPE_ERROR!>CustomFlow<Int><!>): Int = 0

fun <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!>.topLevelFlowExtensionFunction(): Int = 0

fun <!EXPOSED_FLOW_TYPE_ERROR!>SharedFlow<Int><!>.topLevelSharedFlowExtensionFunction(): Int = 0

fun <!EXPOSED_FLOW_TYPE_ERROR!>StateFlow<Int><!>.topLevelStateFlowExtensionFunction(): Int = 0

fun <!EXPOSED_FLOW_TYPE_ERROR!>CustomFlow<Int><!>.topLevelCustomFlowExtensionFunction(): Int = 0

fun topLevelFlowBlockFunction(block: <!EXPOSED_FLOW_TYPE_ERROR!>() -> Flow<Int><!>): Int = 0

fun topLevelFlowArgBlockFunction(block: <!EXPOSED_FLOW_TYPE_ERROR!>(Flow<Int>) -> Int<!>): Int = 0

fun topLevelFlowExtensionBlockFunction(block: <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int>.() -> Int<!>): Int = 0

val topLevelFlowProperty: <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!> get() = throw Throwable()

val topLevelSharedFlowProperty: <!EXPOSED_FLOW_TYPE_ERROR!>SharedFlow<Int><!> get() = throw Throwable()

val topLevelStateFlowProperty: <!EXPOSED_STATE_FLOW_PROPERTY_ERROR!>StateFlow<Int><!> get() = throw Throwable()

val topLevelCustomFlowProperty: <!EXPOSED_FLOW_TYPE_ERROR!>CustomFlow<Int><!> get() = throw Throwable()

val topLevelCustomStateFlowProperty: <!EXPOSED_STATE_FLOW_PROPERTY_ERROR!>CustomStateFlow<Int><!> get() = throw Throwable()

@OptIn(ExperimentalObjCRefinement::class)
@ShouldRefineInSwift
val topLevelRefinedFlowProperty: Flow<Int> get() = throw Throwable()

interface TestInterface {

    <!EXPOSED_SUSPEND_FUNCTION_ERROR!>suspend<!> fun suspendInterfaceFunction(): Int

    val flowInterfaceProperty: <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!>
}

class TestClassA: TestInterface {

    override suspend fun suspendInterfaceFunction(): Int = 0

    override val flowInterfaceProperty: Flow<Int> get() = throw Throwable()

    protected <!EXPOSED_SUSPEND_FUNCTION_ERROR!>suspend<!> fun protectedSuspendFunction(): Int = 0

    protected val protectedFlowProperty: <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!> get() = throw Throwable()

    protected val protectedStateFlowProperty: <!EXPOSED_STATE_FLOW_PROPERTY_ERROR!>StateFlow<Int><!> get() = throw Throwable()

    fun <!EXPOSED_FLOW_TYPE_ERROR!>implicitFlowFunction<!>() = flowInterfaceProperty

    private suspend fun privateSuspendFunction(): Int = 0

    private val privateFlowProperty: Flow<Int> get() = throw Throwable()
}

internal class TestClassB {

    suspend fun suspendFunction(): Int = 0

    val flowProperty: Flow<Int> get() = throw Throwable()
}

expect class TestClassC {
    <!EXPOSED_SUSPEND_FUNCTION_ERROR!>suspend<!> fun suspendFunction(): Int

    val flowProperty: <!EXPOSED_FLOW_TYPE_ERROR!>Flow<Int><!>
}

actual class TestClassC {
    actual suspend fun suspendFunction(): Int = 0

    actual val flowProperty: Flow<Int> get() = throw Throwable()
}
