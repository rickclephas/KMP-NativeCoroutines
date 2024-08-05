// FIR_IDENTICAL
// K2_MODE

// FILE: customFlows.kt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CustomFlow<out T>: Flow<T>

interface CustomStateFlow<out T>: StateFlow<T>

// FILE: customCoroutineScope.kt

import kotlinx.coroutines.CoroutineScope

interface CustomCoroutineScope: CoroutineScope

// FILE: test.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutineScope
val propertyA: CoroutineScope get() = throw Throwable()

@NativeCoroutineScope
val propertyB: CustomCoroutineScope get() = throw Throwable()

<!INVALID_COROUTINE_SCOPE!>@NativeCoroutineScope<!>
val propertyC: Int = 0

@NativeCoroutines
suspend fun functionA(): Int = 0

@NativeCoroutines
fun functionB(): Flow<Int> = throw Throwable()

@NativeCoroutines
fun functionC(): CustomStateFlow<Int> = throw Throwable()

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun functionD(): Int = 0

@NativeCoroutinesIgnore
suspend fun functionE(): Int = 0

@NativeCoroutinesIgnore
fun functionF(): Flow<Int> = throw Throwable()

@NativeCoroutinesIgnore
fun functionG(): CustomStateFlow<Int> = throw Throwable()

<!INVALID_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
fun functionH(): Int = 0

@NativeCoroutinesRefined
suspend fun functionI(): Int = 0

@NativeCoroutinesRefined
fun functionJ(): Flow<Int> = throw Throwable()

@NativeCoroutinesRefined
fun functionK(): CustomStateFlow<Int> = throw Throwable()

<!INVALID_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
fun functionL(): Int = 0

@NativeCoroutines
val flowPropertyA: Flow<Int> get() = throw Throwable()

@NativeCoroutines
val flowPropertyB: CustomStateFlow<Int> get() = throw Throwable()

<!INVALID_COROUTINES!>@NativeCoroutines<!>
val flowPropertyC: Int = 0

@NativeCoroutinesIgnore
val flowPropertyD: Flow<Int> get() = throw Throwable()

@NativeCoroutinesIgnore
val flowPropertyE: CustomStateFlow<Int> get() = throw Throwable()

<!INVALID_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
val flowPropertyF: Int = 0

@NativeCoroutinesState
val flowPropertyG: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesState
val flowPropertyH: CustomStateFlow<Int> get() = throw Throwable()

<!INVALID_COROUTINES_STATE!>@NativeCoroutinesState<!>
val flowPropertyI: Flow<Int> get() = throw Throwable()

@NativeCoroutinesRefined
val flowPropertyJ: Flow<Int> get() = throw Throwable()

@NativeCoroutinesRefined
val flowPropertyK: CustomStateFlow<Int> get() = throw Throwable()

<!INVALID_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
val flowPropertyL: Int = 0

@NativeCoroutinesRefinedState
val flowPropertyM: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesRefinedState
val flowPropertyN: CustomStateFlow<Int> get() = throw Throwable()

<!INVALID_COROUTINES_REFINED_STATE!>@NativeCoroutinesRefinedState<!>
val flowPropertyO: Flow<Int> get() = throw Throwable()

@NativeCoroutines
fun topLevelFlowArgFunction(flow: Flow<Int>): Int = 0

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun topLevelSharedFlowArgFunction(flow: SharedFlow<Int>): Int = 0

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun topLevelStateFlowArgFunction(flow: StateFlow<Int>): Int = 0

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun topLevelCustomFlowArgFunction(flow: CustomFlow<Int>): Int = 0

@NativeCoroutines
fun Flow<Int>.topLevelFlowExtensionFunction(): Int = 0

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun SharedFlow<Int>.topLevelSharedFlowExtensionFunction(): Int = 0

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun StateFlow<Int>.topLevelStateFlowExtensionFunction(): Int = 0

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun CustomFlow<Int>.topLevelCustomFlowExtensionFunction(): Int = 0

@NativeCoroutines
val (StateFlow<Int>.() -> Int).stateFlowFunArgExtensionPropertyA: Int get() = throw Throwable()

@NativeCoroutines
val ((StateFlow<Int>) -> Int).stateFlowFunArgExtensionPropertyB: Int get() = throw Throwable()

<!INVALID_COROUTINES!>@NativeCoroutines<!>
val (() -> StateFlow<Int>).stateFlowFunResultExtensionPropertyA: Int get() = throw Throwable()

<!INVALID_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
val (() -> StateFlow<Int>).stateFlowFunResultExtensionPropertyC: Int get() = throw Throwable()

<!INVALID_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
val (() -> StateFlow<Int>).stateFlowFunResultExtensionPropertyB: Int get() = throw Throwable()

@NativeCoroutines
fun stateFlowFunArgParamFunctionA(block: (StateFlow<Int>.() -> Int)): Int = throw Throwable()

@NativeCoroutines
fun stateFlowFunArgParamFunctionB(block: ((StateFlow<Int>) -> Int)): Int = throw Throwable()

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun stateFlowFunResultParamFunctionA(block: (() -> StateFlow<Int>)): Int = throw Throwable()

<!INVALID_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
fun stateFlowFunResultParamFunctionB(block: (() -> StateFlow<kotlin.Int>)): Int = throw Throwable()

<!INVALID_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
fun stateFlowFunResultParamFunctionC(block: (() -> StateFlow<Int>)): Int = throw Throwable()

<!INVALID_COROUTINES!>@NativeCoroutines<!>
val stateFlowFunArgPropertyA: (StateFlow<Int>.() -> Int) get() = throw Throwable()

<!INVALID_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
val stateFlowFunArgPropertyB: (StateFlow<Int>.() -> Int) get() = throw Throwable()

<!INVALID_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
val stateFlowFunArgPropertyC: (StateFlow<Int>.() -> Int) get() = throw Throwable()

<!INVALID_COROUTINES!>@NativeCoroutines<!>
fun stateFlowFunArgFunctionA(): ((StateFlow<Int>) -> Int) = throw Throwable()

<!INVALID_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
fun stateFlowFunArgFunctionB(): ((StateFlow<Int>) -> Int) = throw Throwable()

<!INVALID_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
fun stateFlowFunArgFunctionC(): ((StateFlow<Int>) -> Int) = throw Throwable()

@NativeCoroutines
val stateFlowFunResultProperty: (() -> StateFlow<Int>) get() = throw Throwable()

@NativeCoroutines
fun stateFlowFunResultFunction(): (() -> StateFlow<Int>) = throw Throwable()
