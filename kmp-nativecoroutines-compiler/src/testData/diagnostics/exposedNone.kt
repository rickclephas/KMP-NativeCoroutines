// FIR_IDENTICAL
// DIAGNOSTICS: -NOT_A_MULTIPLATFORM_COMPILATION -EXPECT_AND_ACTUAL_IN_THE_SAME_MODULE
// EXPOSED_SEVERITY: NONE
// K2_MODE

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

suspend fun topLevelSuspendFunction(): Int = 0

fun topLevelFlowFunction(): Flow<Int> = throw Throwable()

fun topLevelSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

fun topLevelStateFlowFunction(): StateFlow<Int> = throw Throwable()

fun topLevelCustomFlowFunction(): CustomFlow<Int> = throw Throwable()

suspend fun topLevelSuspendFlowFunction(): Flow<Int> = throw Throwable()

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
suspend fun topLevelRefinedSuspendFunction(): Int = 0

fun topLevelSuspendTypeFunction(): (suspend () -> Int) = throw Throwable()

fun topLevelSuspendTypeArgFunction(block: suspend () -> Int): Int = 0

fun (suspend () -> Int).topLevelSuspendTypeExtensionFunction(): Int = 0

fun topLevelFlowArgFunction(flow: Flow<Int>): Int = 0

fun Flow<Int>.topLevelFlowExtensionFunction(): Int = 0

fun topLevelFlowBlockFunction(block: () -> Flow<Int>): Int = 0

fun topLevelFlowArgBlockFunction(block: (Flow<Int>) -> Int): Int = 0

fun topLevelFlowExtensionBlockFunction(block: Flow<Int>.() -> Int): Int = 0

val topLevelFlowProperty: Flow<Int> get() = throw Throwable()

val topLevelSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

val topLevelStateFlowProperty: StateFlow<Int> get() = throw Throwable()

val topLevelCustomFlowProperty: CustomFlow<Int> get() = throw Throwable()

val topLevelCustomStateFlowProperty: CustomStateFlow<Int> get() = throw Throwable()

@OptIn(ExperimentalObjCRefinement::class)
@ShouldRefineInSwift
val topLevelRefinedFlowProperty: Flow<Int> get() = throw Throwable()

interface TestInterface {

    suspend fun suspendInterfaceFunction(): Int

    val flowInterfaceProperty: Flow<Int>
}

class TestClassA: TestInterface {

    override suspend fun suspendInterfaceFunction(): Int = 0

    override val flowInterfaceProperty: Flow<Int> get() = throw Throwable()

    protected suspend fun protectedSuspendFunction(): Int = 0

    protected val protectedFlowProperty: Flow<Int> get() = throw Throwable()

    protected val protectedStateFlowProperty: StateFlow<Int> get() = throw Throwable()

    fun implicitFlowFunction() = flowInterfaceProperty

    private suspend fun privateSuspendFunction(): Int = 0

    private val privateFlowProperty: Flow<Int> get() = throw Throwable()
}

internal class TestClassB {

    suspend fun suspendFunction(): Int = 0

    val flowProperty: Flow<Int> get() = throw Throwable()
}

expect class TestClassC {
    suspend fun suspendFunction(): Int

    val flowProperty: Flow<Int>
}

actual class TestClassC {
    actual suspend fun suspendFunction(): Int = 0

    actual val flowProperty: Flow<Int> get() = throw Throwable()
}
