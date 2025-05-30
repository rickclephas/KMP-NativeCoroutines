package com.rickclephas.kmp.nativecoroutines.idea.quickfixes.k2

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
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.k2.AddAnnotationFixFactory.Companion.registerAddAnnotationFix
import com.rickclephas.kmp.nativecoroutines.idea.quickfixes.k2.RemoveAnnotationFixFactory.Companion.registerRemoveAnnotationFix
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaCompilerPluginDiagnostic0
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KotlinQuickFixFactory
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KotlinQuickFixRegistrar
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KotlinQuickFixesList
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KtQuickFixesListBuilder
import org.jetbrains.kotlin.idea.quickfix.SpecifyTypeExplicitlyFix

internal class KmpNativeCoroutinesQuickFixRegistrar: KotlinQuickFixRegistrar() {
    override val list: KotlinQuickFixesList = KtQuickFixesListBuilder.registerPsiQuickFix {
        registerAddAnnotationFix(
            listOf(EXPOSED_FLOW_TYPE, EXPOSED_FLOW_TYPE_ERROR, EXPOSED_SUSPEND_FUNCTION, EXPOSED_SUSPEND_FUNCTION_ERROR),
            NativeCoroutinesAnnotation.NativeCoroutines
        )

        registerAddAnnotationFix(
            listOf(EXPOSED_STATE_FLOW_PROPERTY, EXPOSED_STATE_FLOW_PROPERTY_ERROR),
            NativeCoroutinesAnnotation.NativeCoroutinesState,
            NativeCoroutinesAnnotation.NativeCoroutines,
        )
        registerRemoveAnnotationFix(
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

        registerFactory(KotlinQuickFixFactory.IntentionBased { diagnostic: KaCompilerPluginDiagnostic0 ->
            if (diagnostic.factoryName != IMPLICIT_RETURN_TYPE.name) return@IntentionBased emptyList()
            listOf(SpecifyTypeExplicitlyFix())
        })
    }
}
