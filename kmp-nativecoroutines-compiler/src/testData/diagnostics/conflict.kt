// FIR_IDENTICAL
// K2_MODE

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutines
val stateFlowPropertyA: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesRefined
val stateFlowPropertyB: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesRefinedState
val stateFlowPropertyC: StateFlow<Int> get() = throw Throwable()

@NativeCoroutinesState
val stateFlowPropertyD: StateFlow<Int> get() = throw Throwable()

<!CONFLICT_COROUTINES!>@NativeCoroutines<!>
<!CONFLICT_COROUTINES!>@NativeCoroutinesRefined<!>
val stateFlowPropertyE: StateFlow<Int> get() = throw Throwable()

<!CONFLICT_COROUTINES!>@NativeCoroutines<!>
<!CONFLICT_COROUTINES!>@NativeCoroutinesRefinedState<!>
val stateFlowPropertyF: StateFlow<Int> get() = throw Throwable()

<!CONFLICT_COROUTINES!>@NativeCoroutines<!>
<!CONFLICT_COROUTINES!>@NativeCoroutinesState<!>
val stateFlowPropertyG: StateFlow<Int> get() = throw Throwable()

<!CONFLICT_COROUTINES!>@NativeCoroutinesRefined<!>
<!CONFLICT_COROUTINES!>@NativeCoroutinesRefinedState<!>
val stateFlowPropertyH: StateFlow<Int> get() = throw Throwable()

<!CONFLICT_COROUTINES!>@NativeCoroutinesRefined<!>
<!CONFLICT_COROUTINES!>@NativeCoroutinesState<!>
val stateFlowPropertyI: StateFlow<Int> get() = throw Throwable()

<!CONFLICT_COROUTINES!>@NativeCoroutinesRefinedState<!>
<!CONFLICT_COROUTINES!>@NativeCoroutinesState<!>
val stateFlowPropertyJ: StateFlow<Int> get() = throw Throwable()
