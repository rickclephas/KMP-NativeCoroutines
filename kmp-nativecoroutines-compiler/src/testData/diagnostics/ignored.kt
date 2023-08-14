import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutines
suspend fun suspendFunctionA(): Int = 0

@NativeCoroutinesIgnore
suspend fun suspendFunctionB(): Int = 0

<!IGNORED_COROUTINES!>@NativeCoroutines<!>
@NativeCoroutinesIgnore
suspend fun suspendFunctionC(): Int = 0

@NativeCoroutinesRefined
suspend fun suspendFunctionD(): Int = 0

@NativeCoroutinesIgnore
suspend fun suspendFunctionE(): Int = 0

<!IGNORED_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
@NativeCoroutinesIgnore
suspend fun suspendFunctionF(): Int = 0

@NativeCoroutinesRefinedState
val stateFlowPropertyA: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesIgnore
val stateFlowPropertyB: StateFlow<Int> get() = throw Throwable()

<!IGNORED_COROUTINES_REFINED_STATE!>@NativeCoroutinesRefinedState<!>
@NativeCoroutinesIgnore
val stateFlowPropertyC: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesState
val stateFlowPropertyD: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesIgnore
val stateFlowPropertyE: StateFlow<Int> get() = throw Throwable()

<!IGNORED_COROUTINES_STATE!>@NativeCoroutinesState<!>
@NativeCoroutinesIgnore
val stateFlowPropertyF: StateFlow<Int> get() = throw Throwable()
