package com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics

import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.CONFLICT_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.IMPLICIT_RETURN_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.UNSUPPORTED_CLASS_EXTENSION_PROPERTY
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory

@Suppress("DuplicatedCode")
internal object FirDefaultErrorMessages: BaseDiagnosticRendererFactory() {
    override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("KmpNativeCoroutines") { map ->
        map.put(CONFLICT_COROUTINES, "NativeCoroutines, NativeCoroutinesRefined, NativeCoroutinesRefinedState and NativeCoroutinesState can't be combined")

        map.put(EXPOSED_FLOW_TYPE, "Flow type is exposed to ObjC")
        map.put(EXPOSED_FLOW_TYPE_ERROR, "Flow type is exposed to ObjC")
        map.put(EXPOSED_STATE_FLOW_PROPERTY, "StateFlow property is exposed to ObjC")
        map.put(EXPOSED_STATE_FLOW_PROPERTY_ERROR, "StateFlow property is exposed to ObjC")
        map.put(EXPOSED_SUSPEND_FUNCTION, "suspend function is exposed to ObjC")
        map.put(EXPOSED_SUSPEND_FUNCTION_ERROR, "suspend function is exposed to ObjC")

        map.put(IGNORED_COROUTINES, "NativeCoroutinesIgnore overrides NativeCoroutines")
        map.put(IGNORED_COROUTINES_REFINED, "NativeCoroutinesIgnore overrides NativeCoroutinesRefined")
        map.put(IGNORED_COROUTINES_REFINED_STATE, "NativeCoroutinesIgnore overrides NativeCoroutinesRefinedState")
        map.put(IGNORED_COROUTINES_STATE, "NativeCoroutinesIgnore overrides NativeCoroutinesState")

        map.put(INVALID_COROUTINES, "NativeCoroutines is only supported on suspend-functions and Flow declarations")
        map.put(INVALID_COROUTINES_IGNORE, "NativeCoroutinesIgnore is only supported on suspend-functions and Flow declarations")
        map.put(INVALID_COROUTINES_REFINED, "NativeCoroutinesRefined is only supported on suspend-functions and Flow declarations")
        map.put(INVALID_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState is only supported on StateFlow properties")
        map.put(INVALID_COROUTINES_STATE, "NativeCoroutinesState is only supported on StateFlow properties")
        map.put(INVALID_COROUTINE_SCOPE, "NativeCoroutineScope is only supported on CoroutineScope properties")

        map.put(INCOMPATIBLE_OVERRIDE_COROUTINES, "NativeCoroutines isn't applied to overridden declaration")
        map.put(INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE, "NativeCoroutinesIgnore isn't applied to overridden declaration")
        map.put(INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED, "NativeCoroutinesRefined isn't applied to overridden declaration")
        map.put(INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState isn't applied to overridden declaration")
        map.put(INCOMPATIBLE_OVERRIDE_COROUTINES_STATE, "NativeCoroutinesState isn't applied to overridden declaration")

        map.put(INCOMPATIBLE_ACTUAL_COROUTINES, "NativeCoroutines isn't applied to expect declaration")
        map.put(INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE, "NativeCoroutinesIgnore isn't applied to expect declaration")
        map.put(INCOMPATIBLE_ACTUAL_COROUTINES_REFINED, "NativeCoroutinesRefined isn't applied to expect declaration")
        map.put(INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState isn't applied to expect declaration")
        map.put(INCOMPATIBLE_ACTUAL_COROUTINES_STATE, "NativeCoroutinesState isn't applied to expect declaration")

        map.put(REDUNDANT_PRIVATE_COROUTINES, "NativeCoroutines is only supported on public declarations")
        map.put(REDUNDANT_PRIVATE_COROUTINES_IGNORE, "NativeCoroutinesIgnore is only supported on public declarations")
        map.put(REDUNDANT_PRIVATE_COROUTINES_REFINED, "NativeCoroutinesRefined is only supported on public declarations")
        map.put(REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE, "NativeCoroutinesRefinedState is only supported on public declarations")
        map.put(REDUNDANT_PRIVATE_COROUTINES_STATE, "NativeCoroutinesState is only supported on public declarations")

        map.put(UNSUPPORTED_CLASS_EXTENSION_PROPERTY, "Class extension properties aren't supported")

        map.put(IMPLICIT_RETURN_TYPE, "Return type must be specified for NativeCoroutines declarations")
    }
}
