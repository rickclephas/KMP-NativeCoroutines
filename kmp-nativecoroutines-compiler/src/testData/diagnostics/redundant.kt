// FIR_IDENTICAL

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlin.experimental.ExperimentalObjCRefinement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TestInterface {

    @NativeCoroutines
    suspend fun functionA(): Int

    @NativeCoroutines
    fun functionB(): Flow<Int>

    @NativeCoroutinesIgnore
    suspend fun functionC(): Int

    @NativeCoroutinesRefined
    suspend fun functionF(): Int

    @NativeCoroutinesRefined
    fun functionG(): Flow<Int>

    @OptIn(ExperimentalObjCRefinement::class)
    @ShouldRefineInSwift
    <!REDUNDANT_REFINED_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
    suspend fun functionI(): Int

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    <!REDUNDANT_REFINED_COROUTINES!>@NativeCoroutines<!>
    suspend fun functionJ(): Int

    @NativeCoroutinesState
    val propertyA: StateFlow<Int>

    @NativeCoroutinesRefinedState
    val propertyC: StateFlow<Int>
}

internal class TestClass: TestInterface {

    @NativeCoroutines
    override suspend fun functionA(): Int = 0

    override fun functionB(): Flow<Int> = throw Throwable()

    @NativeCoroutinesIgnore
    override suspend fun functionC(): Int = 0

    @NativeCoroutinesRefined
    override suspend fun functionF(): Int = 0

    override fun functionG(): Flow<Int> = throw Throwable()

    @NativeCoroutinesState
    override val propertyA: StateFlow<Int> get() = throw Throwable()

    @NativeCoroutinesRefinedState
    override val propertyC: StateFlow<Int> get() = throw Throwable()

    <!REDUNDANT_PRIVATE_COROUTINES!>@NativeCoroutines<!>
    private suspend fun functionD(): Int = 0

    <!REDUNDANT_PRIVATE_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
    internal fun functionE(): Flow<Int> = throw Throwable()

    <!REDUNDANT_PRIVATE_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
    private suspend fun functionH(): Int = 0

    <!REDUNDANT_PRIVATE_COROUTINES_STATE!>@NativeCoroutinesState<!>
    private val propertyB: StateFlow<Int> get() = throw Throwable()

    <!REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE!>@NativeCoroutinesRefinedState<!>
    private val propertyD: StateFlow<Int> get() = throw Throwable()

    override suspend fun functionI(): Int = 0

    override suspend fun functionJ(): Int = 0
}

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class HiddenFromObjCClass {

    <!REDUNDANT_REFINED_COROUTINES!>@NativeCoroutines<!>
    suspend fun functionA(): Int = 0

    <!REDUNDANT_REFINED_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
    suspend fun functionB(): Int = 0

    <!REDUNDANT_REFINED_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
    suspend fun functionC(): Int = 0

    <!REDUNDANT_REFINED_COROUTINES_STATE!>@NativeCoroutinesState<!>
    val propertyA: StateFlow<Int> get() = throw Throwable()

    <!REDUNDANT_REFINED_COROUTINES_REFINED_STATE!>@NativeCoroutinesRefinedState<!>
    val propertyB: StateFlow<Int> get() = throw Throwable()
}
