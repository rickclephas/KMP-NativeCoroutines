// LANGUAGE: +MultiPlatformProjects
// DIAGNOSTICS: -INCOMPATIBLE_OBJC_REFINEMENT_OVERRIDE -NO_ACTUAL_FOR_EXPECT
// EXPOSED_SEVERITY: NONE
// K2_MODE

// MODULE: common
// FILE: common.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

interface InterfaceA {
    suspend fun functionA(): Int

    suspend fun functionB(): Int

    suspend fun functionC(): Int

    val propertyA: StateFlow<Int>

    val propertyB: StateFlow<Int>
}

class ClassA: InterfaceA {
    <!INCOMPATIBLE_OVERRIDE_COROUTINES, INCOMPATIBLE_OVERRIDE_COROUTINES{NATIVE}!>@NativeCoroutines<!>
    override suspend fun functionA(): Int = 0

    <!INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE, INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE{NATIVE}!>@NativeCoroutinesIgnore<!>
    override suspend fun functionB(): Int = 0

    <!INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED, INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED{NATIVE}!>@NativeCoroutinesRefined<!>
    override suspend fun functionC(): Int = 0

    <!INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE, INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE{NATIVE}!>@NativeCoroutinesRefinedState<!>
    override val propertyA: StateFlow<Int> = throw Throwable()

    <!INCOMPATIBLE_OVERRIDE_COROUTINES_STATE, INCOMPATIBLE_OVERRIDE_COROUTINES_STATE{NATIVE}!>@NativeCoroutinesState<!>
    override val propertyB: StateFlow<Int> = throw Throwable()
}

expect class ClassB {
    suspend fun functionA(): Int

    suspend fun functionB(): Int

    suspend fun functionC(): Int

    val propertyA: StateFlow<Int>

    val propertyB: StateFlow<Int>
}

// MODULE: platform()()(common)
// FILE: platform.kt

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

actual class ClassB {
    <!INCOMPATIBLE_ACTUAL_COROUTINES!>@NativeCoroutines<!>
    actual suspend fun functionA(): Int = 0

    <!INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
    actual suspend fun functionB(): Int = 0

    <!INCOMPATIBLE_ACTUAL_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
    actual suspend fun functionC(): Int = 0

    <!INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE!>@NativeCoroutinesRefinedState<!>
    actual val propertyA: StateFlow<Int> = throw Throwable()

    <!INCOMPATIBLE_ACTUAL_COROUTINES_STATE!>@NativeCoroutinesState<!>
    actual val propertyB: StateFlow<Int> = throw Throwable()
}
