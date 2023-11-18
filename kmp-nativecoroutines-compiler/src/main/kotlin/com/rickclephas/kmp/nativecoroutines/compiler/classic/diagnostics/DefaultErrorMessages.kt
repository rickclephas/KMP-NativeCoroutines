package com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics

import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.CONFLICT_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.UNSUPPORTED_CLASS_EXTENSION_PROPERTY
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap

@Suppress("DuplicatedCode")
internal object DefaultErrorMessages : DefaultErrorMessages.Extension {
    override fun getMap(): DiagnosticFactoryToRendererMap = DiagnosticFactoryToRendererMap("KmpNativeCoroutines").apply {
        put(CONFLICT_COROUTINES, "NativeCoroutines, NativeCoroutinesRefined, NativeCoroutinesRefinedState and NativeCoroutinesState can't be combined")
        put(EXPOSED_FLOW_TYPE, "Flow type is exposed to ObjC")
        put(EXPOSED_FLOW_TYPE_ERROR, "Flow type is exposed to ObjC")
        put(EXPOSED_STATE_FLOW_PROPERTY, "StateFlow property is exposed to ObjC")
        put(EXPOSED_STATE_FLOW_PROPERTY_ERROR, "StateFlow property is exposed to ObjC")
        put(EXPOSED_SUSPEND_FUNCTION, "suspend function is exposed to ObjC")
        put(EXPOSED_SUSPEND_FUNCTION_ERROR, "suspend function is exposed to ObjC")
        put(IGNORED_COROUTINES, "NativeCoroutinesIgnore overrides NativeCoroutines")
        put(IGNORED_COROUTINES_REFINED, "NativeCoroutinesIgnore overrides NativeCoroutinesRefined")
        put(IGNORED_COROUTINES_REFINED_STATE, "NativeCoroutinesIgnore overrides NativeCoroutinesRefinedState")
        put(IGNORED_COROUTINES_STATE, "NativeCoroutinesIgnore overrides NativeCoroutinesState")
        put(INVALID_COROUTINES, "NativeCoroutines is only supported on suspend-functions and Flow declarations")
        put(INVALID_COROUTINES_IGNORE, "NativeCoroutinesIgnore is only supported on suspend-functions and Flow declarations")
        put(INVALID_COROUTINES_REFINED, "NativeCoroutinesRefined is only supported on suspend-functions and Flow declarations")
        put(INVALID_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState is only supported on StateFlow properties")
        put(INVALID_COROUTINES_STATE, "NativeCoroutinesState is only supported on StateFlow properties")
        put(INVALID_COROUTINE_SCOPE, "NativeCoroutineScope is only supported on CoroutineScope properties")
        put(REDUNDANT_OVERRIDE_COROUTINES, "NativeCoroutines should be applied to overridden declaration instead")
        put(REDUNDANT_OVERRIDE_COROUTINES_IGNORE, "NativeCoroutinesIgnore isn't supported on overridden declarations")
        put(REDUNDANT_OVERRIDE_COROUTINES_REFINED, "NativeCoroutinesRefined should be applied to overridden declaration instead")
        put(REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState should be applied to overridden declaration instead")
        put(REDUNDANT_OVERRIDE_COROUTINES_STATE, "NativeCoroutinesState should be applied to overridden declaration instead")
        put(REDUNDANT_PRIVATE_COROUTINES, "NativeCoroutines is only supported on public declarations")
        put(REDUNDANT_PRIVATE_COROUTINES_IGNORE, "NativeCoroutinesIgnore is only supported on public declarations")
        put(REDUNDANT_PRIVATE_COROUTINES_REFINED, "NativeCoroutinesRefined is only supported on public declarations")
        put(REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState is only supported on public declarations")
        put(REDUNDANT_PRIVATE_COROUTINES_STATE, "NativeCoroutinesState is only supported on public declarations")
        put(UNSUPPORTED_CLASS_EXTENSION_PROPERTY, "Class extension properties aren't supported")
    }
}
