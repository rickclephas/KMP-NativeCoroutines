// FIR_IDENTICAL

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.StateFlow

class TestClass {

    <!UNSUPPORTED_CLASS_EXTENSION_PROPERTY!>@NativeCoroutines<!>
    val Int.extensionPropertyA: StateFlow<Int> get() = throw Throwable()

    @NativeCoroutinesIgnore
    val Int.extensionPropertyE: StateFlow<Int> get() = throw Throwable()

    <!UNSUPPORTED_CLASS_EXTENSION_PROPERTY!>@NativeCoroutinesRefined<!>
    val Int.extensionPropertyB: StateFlow<Int> get() = throw Throwable()

    <!UNSUPPORTED_CLASS_EXTENSION_PROPERTY!>@NativeCoroutinesRefinedState<!>
    val Int.extensionPropertyC: StateFlow<Int> get() = throw Throwable()

    <!UNSUPPORTED_CLASS_EXTENSION_PROPERTY!>@NativeCoroutinesState<!>
    val Int.extensionPropertyD: StateFlow<Int> get() = throw Throwable()
}
