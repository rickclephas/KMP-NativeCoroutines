import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
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

    @NativeCoroutinesState
    val propertyA: StateFlow<Int>

    @NativeCoroutinesRefinedState
    val propertyC: StateFlow<Int>
}

class TestClass: TestInterface {

    <!REDUNDANT_OVERRIDE_COROUTINES!>@NativeCoroutines<!>
    override suspend fun functionA(): Int = 0

    override fun functionB(): Flow<Int> = throw Throwable()

    <!REDUNDANT_OVERRIDE_COROUTINES_IGNORE!>@NativeCoroutinesIgnore<!>
    override suspend fun functionC(): Int = 0

    <!REDUNDANT_OVERRIDE_COROUTINES_REFINED!>@NativeCoroutinesRefined<!>
    override suspend fun functionF(): Int = 0

    override fun functionG(): Flow<Int> = throw Throwable()

    <!REDUNDANT_OVERRIDE_COROUTINES_STATE!>@NativeCoroutinesState<!>
    override val propertyA: StateFlow<Int> get() = throw Throwable()

    <!REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE!>@NativeCoroutinesRefinedState<!>
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
}
