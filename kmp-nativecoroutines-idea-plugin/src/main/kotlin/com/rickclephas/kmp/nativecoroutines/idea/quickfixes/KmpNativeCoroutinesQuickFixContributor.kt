package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.CONFLICT_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.AddAnnotationFixFactory.Companion.registerAddAnnotationFix
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.RemoveAnnotationFixFactory.Companion.registerRemoveAnnotationFix
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.idea.quickfix.QuickFixes

public class KmpNativeCoroutinesQuickFixContributor: QuickFixContributor {
    override fun registerQuickFixes(quickFixes: QuickFixes) {
        quickFixes.registerAddAnnotationFix(
            listOf(EXPOSED_FLOW_TYPE, EXPOSED_FLOW_TYPE_ERROR, EXPOSED_SUSPEND_FUNCTION, EXPOSED_SUSPEND_FUNCTION_ERROR),
            NativeCoroutinesAnnotation.NativeCoroutines,
        )
        quickFixes.registerAddAnnotationFix(
            listOf(EXPOSED_STATE_FLOW_PROPERTY, EXPOSED_STATE_FLOW_PROPERTY_ERROR),
            NativeCoroutinesAnnotation.NativeCoroutinesState,
            NativeCoroutinesAnnotation.NativeCoroutines,
        )
        quickFixes.registerRemoveAnnotationFix(
            CONFLICT_COROUTINES,

            IGNORED_COROUTINES,
            IGNORED_COROUTINES_REFINED,
            IGNORED_COROUTINES_REFINED_STATE,
            IGNORED_COROUTINES_STATE,

            INVALID_COROUTINES,
            INVALID_COROUTINE_SCOPE,
            INVALID_COROUTINES_IGNORE,
            INVALID_COROUTINES_REFINED,
            INVALID_COROUTINES_REFINED_STATE,
            INVALID_COROUTINES_STATE,

            INCOMPATIBLE_OVERRIDE_COROUTINES,
            INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE,
            INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED,
            INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE,
            INCOMPATIBLE_OVERRIDE_COROUTINES_STATE,

            INCOMPATIBLE_ACTUAL_COROUTINES,
            INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE,
            INCOMPATIBLE_ACTUAL_COROUTINES_REFINED,
            INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE,
            INCOMPATIBLE_ACTUAL_COROUTINES_STATE,

            REDUNDANT_PRIVATE_COROUTINES,
            REDUNDANT_PRIVATE_COROUTINES_IGNORE,
            REDUNDANT_PRIVATE_COROUTINES_REFINED,
            REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE,
            REDUNDANT_PRIVATE_COROUTINES_STATE,
        )
    }
}
