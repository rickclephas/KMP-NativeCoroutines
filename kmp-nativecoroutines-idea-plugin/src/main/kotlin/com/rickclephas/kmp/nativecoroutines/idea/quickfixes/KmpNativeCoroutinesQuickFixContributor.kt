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
            listOf(EXPOSED_FLOW_TYPE, EXPOSED_FLOW_TYPE_ERROR, EXPOSED_SUSPEND_FUNCTION, EXPOSED_SUSPEND_FUNCTION_ERROR),
            FqNames.nativeCoroutines,
        )
        quickFixes.registerAddAnnotationFix(
            listOf(EXPOSED_STATE_FLOW_PROPERTY, EXPOSED_STATE_FLOW_PROPERTY_ERROR),
            FqNames.nativeCoroutinesState,
            FqNames.nativeCoroutines,
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
            REDUNDANT_OVERRIDE_COROUTINES,
            REDUNDANT_OVERRIDE_COROUTINES_IGNORE,
            REDUNDANT_OVERRIDE_COROUTINES_REFINED,
            REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE,
            REDUNDANT_OVERRIDE_COROUTINES_STATE,
            REDUNDANT_PRIVATE_COROUTINES,
            REDUNDANT_PRIVATE_COROUTINES_IGNORE,
            REDUNDANT_PRIVATE_COROUTINES_REFINED,
            REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE,
            REDUNDANT_PRIVATE_COROUTINES_STATE,
        )
    }
}
