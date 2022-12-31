package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.AddAnnotationFixFactory.Companion.registerAddAnnotationFix
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
            FqNames.nativeCoroutinesState,
            EXPOSED_STATE_FLOW_PROPERTY,
            EXPOSED_STATE_FLOW_PROPERTY_ERROR
        )
    }
}
