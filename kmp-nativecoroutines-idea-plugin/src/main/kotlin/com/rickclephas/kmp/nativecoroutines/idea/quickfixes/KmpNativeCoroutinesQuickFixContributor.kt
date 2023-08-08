package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.CONFLICT_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.AddAnnotationFixFactory.Companion.registerAddAnnotationFix
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.RemoveAnnotationFixFactory.Companion.registerRemoveAnnotationFix
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesFqNames as FqNames
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.idea.quickfix.QuickFixes

class KmpNativeCoroutinesQuickFixContributor: QuickFixContributor {
    override fun registerQuickFixes(quickFixes: QuickFixes) {
        quickFixes.registerAddAnnotationFix(
            FqNames.nativeCoroutines,
            EXPOSED_FLOW_TYPE,
            EXPOSED_FLOW_TYPE_ERROR,
            EXPOSED_STATE_FLOW_PROPERTY,
            EXPOSED_STATE_FLOW_PROPERTY_ERROR,
            EXPOSED_SUSPEND_FUNCTION,
            EXPOSED_SUSPEND_FUNCTION_ERROR
        )
        quickFixes.registerAddAnnotationFix(
            FqNames.nativeCoroutinesIgnore,
            EXPOSED_FLOW_TYPE,
            EXPOSED_FLOW_TYPE_ERROR,
            EXPOSED_STATE_FLOW_PROPERTY,
            EXPOSED_STATE_FLOW_PROPERTY_ERROR,
            EXPOSED_SUSPEND_FUNCTION,
            EXPOSED_SUSPEND_FUNCTION_ERROR
        )
        quickFixes.registerAddAnnotationFix(
            FqNames.nativeCoroutinesRefined,
            EXPOSED_FLOW_TYPE,
            EXPOSED_FLOW_TYPE_ERROR,
            EXPOSED_STATE_FLOW_PROPERTY,
            EXPOSED_STATE_FLOW_PROPERTY_ERROR,
            EXPOSED_SUSPEND_FUNCTION,
            EXPOSED_SUSPEND_FUNCTION_ERROR
        )
        quickFixes.registerAddAnnotationFix(
            FqNames.nativeCoroutinesRefinedState,
            EXPOSED_STATE_FLOW_PROPERTY,
            EXPOSED_STATE_FLOW_PROPERTY_ERROR
        )
        quickFixes.registerAddAnnotationFix(
            FqNames.nativeCoroutinesState,
            EXPOSED_STATE_FLOW_PROPERTY,
            EXPOSED_STATE_FLOW_PROPERTY_ERROR
        )
        quickFixes.registerRemoveAnnotationFix(
            "NativeCoroutinesState",
            CONFLICT_COROUTINES,
            IGNORED_COROUTINES_STATE,
            INVALID_COROUTINES_STATE,
            REDUNDANT_OVERRIDE_COROUTINES_STATE,
            REDUNDANT_PRIVATE_COROUTINES_STATE
        )
        quickFixes.registerRemoveAnnotationFix(
            "NativeCoroutinesRefinedState",
            CONFLICT_COROUTINES,
            IGNORED_COROUTINES_REFINED_STATE,
            INVALID_COROUTINES_REFINED_STATE,
            REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE,
            REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
        )
        quickFixes.registerRemoveAnnotationFix(
            "NativeCoroutinesRefined",
            CONFLICT_COROUTINES,
            IGNORED_COROUTINES_REFINED,
            INVALID_COROUTINES_REFINED,
            REDUNDANT_OVERRIDE_COROUTINES_REFINED,
            REDUNDANT_PRIVATE_COROUTINES_REFINED
        )
        quickFixes.registerRemoveAnnotationFix(
            "NativeCoroutines",
            CONFLICT_COROUTINES,
            IGNORED_COROUTINES,
            INVALID_COROUTINES,
            REDUNDANT_OVERRIDE_COROUTINES,
            REDUNDANT_PRIVATE_COROUTINES
        )
        quickFixes.registerRemoveAnnotationFix(
            "NativeCoroutinesIgnore",
            INVALID_COROUTINES_IGNORE,
            REDUNDANT_OVERRIDE_COROUTINES_IGNORE,
            REDUNDANT_PRIVATE_COROUTINES_IGNORE
        )
        quickFixes.registerRemoveAnnotationFix(
            "NativeCoroutineScope",
            INVALID_COROUTINE_SCOPE
        )
    }
}
