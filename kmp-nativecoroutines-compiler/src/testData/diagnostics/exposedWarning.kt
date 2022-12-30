// EXPOSED_SEVERITY: WARNING

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

<!EXPOSED_SUSPEND_FUNCTION!>suspend<!> fun topLevelSuspendFunction(): Int = 0

@NativeCoroutines
suspend fun topLevelAnnotatedSuspendFunction() { }

fun topLevelFlowFunction(): <!EXPOSED_FLOW_TYPE!>Flow<Int><!> = throw Throwable()

@NativeCoroutines
fun topLevelAnnotatedFlowFunction(): Flow<Int> = throw Throwable()

fun topLevelSharedFlowFunction(): <!EXPOSED_FLOW_TYPE!>SharedFlow<Int><!> = throw Throwable()

@NativeCoroutines
fun topLevelAnnotatedSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

fun topLevelStateFlowFunction(): <!EXPOSED_FLOW_TYPE!>StateFlow<Int><!> = throw Throwable()

@NativeCoroutines
fun topLevelAnnotatedStateFlowFunction(): StateFlow<Int> = throw Throwable()

<!EXPOSED_SUSPEND_FUNCTION!>suspend<!> fun topLevelSuspendFlowFunction(): <!EXPOSED_FLOW_TYPE!>Flow<Int><!> = throw Throwable()

@NativeCoroutines
suspend fun topLevelAnnotatedSuspendFlowFunction(): Flow<Int> = throw Throwable()

val topLevelFlowProperty: <!EXPOSED_FLOW_TYPE!>Flow<Int><!> get() = throw Throwable()

@NativeCoroutines
val topLevelAnnotatedFlowProperty: Flow<Int> get() = throw Throwable()

val topLevelSharedFlowProperty: <!EXPOSED_FLOW_TYPE!>SharedFlow<Int><!> get() = throw Throwable()

@NativeCoroutines
val topLevelAnnotatedSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

val topLevelStateFlowProperty: <!EXPOSED_FLOW_TYPE!>StateFlow<Int><!> get() = throw Throwable()

@NativeCoroutines
val topLevelAnnotatedStateFlowProperty: StateFlow<Int> get() = throw Throwable()

class TestClass {

    <!EXPOSED_SUSPEND_FUNCTION!>suspend<!> fun suspendFunction(): Int = 0

    @NativeCoroutines
    suspend fun annotatedSuspendFunction() { }

    fun flowFunction(): <!EXPOSED_FLOW_TYPE!>Flow<Int><!> = throw Throwable()

    @NativeCoroutines
    fun annotatedFlowFunction(): Flow<Int> = throw Throwable()

    fun sharedFlowFunction(): <!EXPOSED_FLOW_TYPE!>SharedFlow<Int><!> = throw Throwable()

    @NativeCoroutines
    fun annotatedSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

    fun stateFlowFunction(): <!EXPOSED_FLOW_TYPE!>StateFlow<Int><!> = throw Throwable()

    @NativeCoroutines
    fun annotatedStateFlowFunction(): StateFlow<Int> = throw Throwable()

    <!EXPOSED_SUSPEND_FUNCTION!>suspend<!> fun suspendFlowFunction(): <!EXPOSED_FLOW_TYPE!>Flow<Int><!> = throw Throwable()

    @NativeCoroutines
    suspend fun annotatedSuspendFlowFunction(): Flow<Int> = throw Throwable()

    val flowProperty: <!EXPOSED_FLOW_TYPE!>Flow<Int><!> get() = throw Throwable()

    @NativeCoroutines
    val annotatedFlowProperty: Flow<Int> get() = throw Throwable()

    val sharedFlowProperty: <!EXPOSED_FLOW_TYPE!>SharedFlow<Int><!> get() = throw Throwable()

    @NativeCoroutines
    val annotatedSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

    val stateFlowProperty: <!EXPOSED_FLOW_TYPE!>StateFlow<Int><!> get() = throw Throwable()

    @NativeCoroutines
    val annotatedStateFlowProperty: StateFlow<Int> get() = throw Throwable()
}
