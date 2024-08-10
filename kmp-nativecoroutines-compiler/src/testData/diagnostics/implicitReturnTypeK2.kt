// FIR_IDENTICAL
// K2_MODE

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow

@NativeCoroutines
suspend fun <!IMPLICIT_RETURN_TYPE!>implicitReturnTypeFunction<!>() = 0

@NativeCoroutines
suspend fun explicitReturnTypeFunction(): Int = 0

@NativeCoroutines
suspend fun implicitUnitReturnTypeFunction() { }

@NativeCoroutinesIgnore
suspend fun ignoredImplicitReturnTypeFunction() = 0

@NativeCoroutinesState
val <!IMPLICIT_RETURN_TYPE!>implicitReturnTypeProperty<!> = MutableStateFlow(0)

@NativeCoroutinesState
val explicitReturnTypeProperty: MutableStateFlow<Int> = MutableStateFlow(0)
