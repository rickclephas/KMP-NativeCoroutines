// EXPOSED_SEVERITY: NONE

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

suspend fun topLevelSuspendFunction(): Int = 0

@NativeCoroutines
suspend fun topLevelAnnotatedSuspendFunction() { }

fun topLevelFlowFunction(): Flow<Int> = throw Throwable()

@NativeCoroutines
fun topLevelAnnotatedFlowFunction(): Flow<Int> = throw Throwable()

fun topLevelSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

@NativeCoroutines
fun topLevelAnnotatedSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

fun topLevelStateFlowFunction(): StateFlow<Int> = throw Throwable()

@NativeCoroutines
fun topLevelAnnotatedStateFlowFunction(): StateFlow<Int> = throw Throwable()

suspend fun topLevelSuspendFlowFunction(): Flow<Int> = throw Throwable()

@NativeCoroutines
suspend fun topLevelAnnotatedSuspendFlowFunction(): Flow<Int> = throw Throwable()

val topLevelFlowProperty: Flow<Int> get() = throw Throwable()

@NativeCoroutines
val topLevelAnnotatedFlowProperty: Flow<Int> get() = throw Throwable()

val topLevelSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

@NativeCoroutines
val topLevelAnnotatedSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

val topLevelStateFlowProperty: StateFlow<Int> get() = throw Throwable()

@NativeCoroutines
val topLevelAnnotatedStateFlowProperty: StateFlow<Int> get() = throw Throwable()

class TestClass {

    suspend fun suspendFunction(): Int = 0

    @NativeCoroutines
    suspend fun annotatedSuspendFunction() { }

    fun flowFunction(): Flow<Int> = throw Throwable()

    @NativeCoroutines
    fun annotatedFlowFunction(): Flow<Int> = throw Throwable()

    fun sharedFlowFunction(): SharedFlow<Int> = throw Throwable()

    @NativeCoroutines
    fun annotatedSharedFlowFunction(): SharedFlow<Int> = throw Throwable()

    fun stateFlowFunction(): StateFlow<Int> = throw Throwable()

    @NativeCoroutines
    fun annotatedStateFlowFunction(): StateFlow<Int> = throw Throwable()

    suspend fun topLevelSuspendFlowFunction(): Flow<Int> = throw Throwable()

    @NativeCoroutines
    suspend fun topLevelAnnotatedSuspendFlowFunction(): Flow<Int> = throw Throwable()

    val flowProperty: Flow<Int> get() = throw Throwable()

    @NativeCoroutines
    val annotatedFlowProperty: Flow<Int> get() = throw Throwable()

    val sharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

    @NativeCoroutines
    val annotatedSharedFlowProperty: SharedFlow<Int> get() = throw Throwable()

    val stateFlowProperty: StateFlow<Int> get() = throw Throwable()

    @NativeCoroutines
    val annotatedStateFlowProperty: StateFlow<Int> get() = throw Throwable()
}
