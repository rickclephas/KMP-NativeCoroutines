package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap

internal object DefaultErrorMessages : DefaultErrorMessages.Extension {
    override fun getMap(): DiagnosticFactoryToRendererMap = DiagnosticFactoryToRendererMap("KmpNativeCoroutines").apply {
        put(EXPOSED_FLOW_TYPE, "Flow type is exposed to ObjC")
        put(EXPOSED_SUSPEND_FUNCTION, "suspend function is exposed to ObjC")
        put(IGNORED_COROUTINES, "NativeCoroutinesIgnore overrides NativeCoroutines")
        put(INVALID_COROUTINES, "NativeCoroutines is only supported on suspend-functions and Flow declarations")
        put(INVALID_COROUTINES_IGNORE, "NativeCoroutinesIgnore is only supported on suspend-functions and Flow declarations")
        put(INVALID_COROUTINE_SCOPE, "NativeCoroutineScope is only supported on CoroutineScope properties")
        put(REDUNDANT_OVERRIDE_COROUTINES, "NativeCoroutines isn't supported on overridden declarations")
        put(REDUNDANT_OVERRIDE_COROUTINES_IGNORE, "NativeCoroutinesIgnore isn't supported on overridden declarations")
        put(REDUNDANT_PRIVATE_COROUTINES, "NativeCoroutines is only supported on public declarations")
        put(REDUNDANT_PRIVATE_COROUTINES_IGNORE, "NativeCoroutinesIgnore is only supported on public declarations")
    }
}
