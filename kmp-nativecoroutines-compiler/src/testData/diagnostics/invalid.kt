// FILE: customStateFlow.kt

import kotlinx.coroutines.flow.StateFlow

interface CustomStateFlow<out T>: StateFlow<T>

// FILE: test.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@NativeCoroutineScope
val propertyA: CoroutineScope get() = throw Throwable()

<!INVALID_COROUTINE_SCOPE!>@NativeCoroutineScope<!>
val propertyB: Int = 0

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
