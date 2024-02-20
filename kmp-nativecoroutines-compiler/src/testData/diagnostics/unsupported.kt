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

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
val StateFlow<Int>.stateFlowExtensionPropertyA: Int get() = throw Throwable()

@NativeCoroutinesIgnore
val StateFlow<Int>.stateFlowExtensionPropertyB: Int get() = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutinesRefined<!>
val StateFlow<Int>.stateFlowExtensionPropertyC: Int get() = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun stateFlowFunctionA(flow: StateFlow<Int>): Int = throw Throwable()

@NativeCoroutinesIgnore
fun stateFlowFunctionB(flow: StateFlow<Int>): Int = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutinesRefined<!>
fun stateFlowFunctionC(flow: StateFlow<Int>): Int = throw Throwable()

@NativeCoroutines
val (StateFlow<Int>.() -> Int).stateFlowFunArgExtensionPropertyA: Int get() = throw Throwable()

@NativeCoroutines
val ((StateFlow<Int>) -> Int).stateFlowFunArgExtensionPropertyB: Int get() = throw Throwable()

@NativeCoroutines
fun stateFlowFunArgParamFunctionA(block: (StateFlow<Int>.() -> Int)): Int = throw Throwable()

@NativeCoroutines
fun stateFlowFunArgParamFunctionB(block: ((StateFlow<Int>) -> Int)): Int = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
val (() -> StateFlow<Int>).stateFlowFunResultExtensionPropertyA: Int get() = throw Throwable()

@NativeCoroutinesIgnore
val (() -> StateFlow<Int>).stateFlowFunResultExtensionPropertyB: Int get() = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutinesRefined<!>
val (() -> StateFlow<Int>).stateFlowFunResultExtensionPropertyC: Int get() = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun stateFlowFunResultParamFunctionA(block: (() -> StateFlow<Int>)): Int = throw Throwable()

@NativeCoroutinesIgnore
fun stateFlowFunResultParamFunctionB(block: (() -> StateFlow<kotlin.Int>)): Int = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutinesRefined<!>
fun stateFlowFunResultParamFunctionC(block: (() -> StateFlow<Int>)): Int = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
val stateFlowFunArgPropertyA: (StateFlow<Int>.() -> Int) get() = throw Throwable()

@NativeCoroutinesIgnore
val stateFlowFunArgPropertyB: (StateFlow<Int>.() -> Int) get() = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutinesRefined<!>
val stateFlowFunArgPropertyC: (StateFlow<Int>.() -> Int) get() = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutines<!>
fun stateFlowFunArgFunctionA(): ((StateFlow<Int>) -> Int) = throw Throwable()

@NativeCoroutinesIgnore
fun stateFlowFunArgFunctionB(): ((StateFlow<Int>) -> Int) = throw Throwable()

<!UNSUPPORTED_INPUT_FLOW!>@NativeCoroutinesRefined<!>
fun stateFlowFunArgFunctionC(): ((StateFlow<Int>) -> Int) = throw Throwable()

@NativeCoroutines
val stateFlowFunResultProperty: (() -> StateFlow<Int>) get() = throw Throwable()

@NativeCoroutines
fun stateFlowFunResultFunction(): (() -> StateFlow<Int>) = throw Throwable()
