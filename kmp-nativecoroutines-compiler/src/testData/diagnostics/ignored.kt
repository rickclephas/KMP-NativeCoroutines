import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutines
suspend fun suspendFunctionA(): Int = 0

@NativeCoroutinesIgnore
suspend fun suspendFunctionB(): Int = 0

<!IGNORED_COROUTINES!>@NativeCoroutines<!>
@NativeCoroutinesIgnore
suspend fun suspendFunctionC(): Int = 0

@NativeCoroutinesState
val stateFlowPropertyA: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesIgnore
val stateFlowPropertyB: StateFlow<Int> get() = throw Throwable()

<!IGNORED_COROUTINES_STATE!>@NativeCoroutinesState<!>
@NativeCoroutinesIgnore
val stateFlowPropertyC: StateFlow<Int> get() = throw Throwable()
