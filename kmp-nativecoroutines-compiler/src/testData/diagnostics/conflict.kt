import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutines
val stateFlowPropertyA: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesState
val stateFlowPropertyB: StateFlow<Int> get() = throw Throwable()

@NativeCoroutines
<!CONFLICT_COROUTINES_STATE!>@NativeCoroutinesState<!>
val stateFlowPropertyC: StateFlow<Int> get() = throw Throwable()
