package com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.CONFLICT_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.checkers.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.NativeCoroutinesAnnotations
import org.jetbrains.kotlin.AbstractKtSourceElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirCallableDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.getModifier
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.effectiveVisibility
import org.jetbrains.kotlin.fir.declarations.utils.isOverride
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.lexer.KtTokens

internal class FirKmpNativeCoroutinesDeclarationChecker(
    exposedSeverity: ExposedSeverity
): FirCallableDeclarationChecker() {

    private val exposedSuspendFunction = when (exposedSeverity) {
        ExposedSeverity.NONE -> null
        ExposedSeverity.WARNING -> EXPOSED_SUSPEND_FUNCTION
        ExposedSeverity.ERROR -> EXPOSED_SUSPEND_FUNCTION_ERROR
    }

    private val exposedFlowType = when (exposedSeverity) {
        ExposedSeverity.NONE -> null
        ExposedSeverity.WARNING -> EXPOSED_FLOW_TYPE
        ExposedSeverity.ERROR -> EXPOSED_FLOW_TYPE_ERROR
    }

    private val exposedStateFlowProperty = when (exposedSeverity) {
        ExposedSeverity.NONE -> null
        ExposedSeverity.WARNING -> EXPOSED_STATE_FLOW_PROPERTY
        ExposedSeverity.ERROR -> EXPOSED_STATE_FLOW_PROPERTY_ERROR
    }

    override fun check(declaration: FirCallableDeclaration, context: CheckerContext, reporter: DiagnosticReporter) {
        if (declaration !is FirSimpleFunction && declaration !is FirProperty) return

        fun KtDiagnosticFactory0.reportOn(annotation: FirAnnotation?) {
            if (annotation == null) return
            reporter.reportOn(annotation.source, this, context)
        }

        fun KtDiagnosticFactory0?.reportOn(source: AbstractKtSourceElement?) {
            if (this == null) return
            reporter.reportOn(source, this, context)
        }

        val annotations = NativeCoroutinesAnnotations(declaration, context.session)
        val isPublic = declaration.effectiveVisibility.publicApi
        val isOverride = declaration.isOverride
        val isSuspend = declaration.isSuspend

        //region CONFLICT_COROUTINES
        val coroutinesAnnotations = listOf(
            annotations.nativeCoroutines,
            annotations.nativeCoroutinesRefined,
            annotations.nativeCoroutinesRefinedState,
            annotations.nativeCoroutinesState,
        )
        val annotationCount = coroutinesAnnotations.count { it != null }
        if (annotationCount > 1) {
            coroutinesAnnotations.forEach { CONFLICT_COROUTINES.reportOn(it) }
        }
        //endregion

        //region EXPOSED_*
        if (isPublic && !isOverride && annotationCount == 0 && annotations.nativeCoroutinesIgnore == null) {
            if (isSuspend) {
                val source = declaration.getModifier(KtTokens.SUSPEND_KEYWORD)?.source ?: declaration.source
                exposedSuspendFunction.reportOn(source)
            }
            // TODO: Return type exposed checks
        }
        //endregion

        //region IGNORED_*
        if (annotations.nativeCoroutinesIgnore != null) {
            IGNORED_COROUTINES.reportOn(annotations.nativeCoroutines)
            IGNORED_COROUTINES_REFINED.reportOn(annotations.nativeCoroutinesRefined)
            IGNORED_COROUTINES_REFINED_STATE.reportOn(annotations.nativeCoroutinesRefinedState)
            IGNORED_COROUTINES_STATE.reportOn(annotations.nativeCoroutinesState)
        }
        //endregion

        //region INVALID_*
        // TODO: Return type invalid checks
        //endregion

        //region REDUNDANT_*
        if (isOverride) {
            REDUNDANT_OVERRIDE_COROUTINES.reportOn(annotations.nativeCoroutines)
            REDUNDANT_OVERRIDE_COROUTINES_IGNORE.reportOn(annotations.nativeCoroutinesIgnore)
            REDUNDANT_OVERRIDE_COROUTINES_REFINED.reportOn(annotations.nativeCoroutinesRefined)
            REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE.reportOn(annotations.nativeCoroutinesRefinedState)
            REDUNDANT_OVERRIDE_COROUTINES_STATE.reportOn(annotations.nativeCoroutinesState)
        }
        if (!isPublic) {
            REDUNDANT_PRIVATE_COROUTINES.reportOn(annotations.nativeCoroutines)
            REDUNDANT_PRIVATE_COROUTINES_IGNORE.reportOn(annotations.nativeCoroutinesIgnore)
            REDUNDANT_PRIVATE_COROUTINES_REFINED.reportOn(annotations.nativeCoroutinesRefined)
            REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE.reportOn(annotations.nativeCoroutinesRefinedState)
            REDUNDANT_PRIVATE_COROUTINES_STATE.reportOn(annotations.nativeCoroutinesState)
        }
        //endregion
    }
}
