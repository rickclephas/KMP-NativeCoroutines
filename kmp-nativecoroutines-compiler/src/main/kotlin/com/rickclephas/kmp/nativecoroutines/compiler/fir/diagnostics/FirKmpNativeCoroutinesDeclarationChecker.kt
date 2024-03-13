package com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
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
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics.FirKmpNativeCoroutinesErrors.UNSUPPORTED_CLASS_EXTENSION_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.getCoroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.getNativeCoroutinesAnnotations
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesState
import org.jetbrains.kotlin.AbstractKtSourceElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirCallableDeclarationChecker
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.effectiveVisibility
import org.jetbrains.kotlin.fir.declarations.utils.isExtension
import org.jetbrains.kotlin.fir.declarations.utils.isOverride
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.expressions.FirAnnotation

internal class FirKmpNativeCoroutinesDeclarationChecker(
    exposedSeverity: ExposedSeverity
): FirCallableDeclarationChecker(MppCheckerKind.Common) {

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

        val annotations = declaration.getNativeCoroutinesAnnotations(context.session)
        val isPublic = declaration.effectiveVisibility.publicApi
        val isOverride = declaration.isOverride
        val isSuspend = declaration.isSuspend
        val returnType = declaration.getCoroutinesReturnType(context.session)

        //region CONFLICT_COROUTINES
        val coroutinesAnnotations = listOfNotNull(
            annotations[NativeCoroutines],
            annotations[NativeCoroutinesRefined],
            annotations[NativeCoroutinesRefinedState],
            annotations[NativeCoroutinesState],
        )
        if (coroutinesAnnotations.size > 1) {
            coroutinesAnnotations.forEach { CONFLICT_COROUTINES.reportOn(it) }
        }
        //endregion

        //region EXPOSED_*
        if (isPublic && !isOverride && coroutinesAnnotations.isEmpty() && !annotations.containsKey(NativeCoroutinesIgnore)) {
            if (isSuspend) {
                exposedSuspendFunction.reportOn(declaration.source)
            }
            if (returnType is CoroutinesReturnType.Flow) {
                val diagnosticFactory = when {
                    declaration is FirProperty && returnType == CoroutinesReturnType.Flow.State -> exposedStateFlowProperty
                    else -> exposedFlowType
                }
                diagnosticFactory.reportOn(declaration.source)
            }
        }
        //endregion

        //region IGNORED_*
        if (annotations.containsKey(NativeCoroutinesIgnore)) {
            IGNORED_COROUTINES.reportOn(annotations[NativeCoroutines])
            IGNORED_COROUTINES_REFINED.reportOn(annotations[NativeCoroutinesRefined])
            IGNORED_COROUTINES_REFINED_STATE.reportOn(annotations[NativeCoroutinesRefinedState])
            IGNORED_COROUTINES_STATE.reportOn(annotations[NativeCoroutinesState])
        }
        //endregion

        //region INVALID_*
        if (declaration !is FirProperty || returnType != CoroutinesReturnType.CoroutineScope) {
            INVALID_COROUTINE_SCOPE.reportOn(annotations[NativeCoroutineScope])
        }
        if (!isSuspend && returnType !is CoroutinesReturnType.Flow) {
            INVALID_COROUTINES.reportOn(annotations[NativeCoroutines])
            INVALID_COROUTINES_IGNORE.reportOn(annotations[NativeCoroutinesIgnore])
            INVALID_COROUTINES_REFINED.reportOn(annotations[NativeCoroutinesRefined])
        }
        if (declaration !is FirProperty || returnType !is CoroutinesReturnType.Flow.State) {
            INVALID_COROUTINES_REFINED_STATE.reportOn(annotations[NativeCoroutinesRefinedState])
            INVALID_COROUTINES_STATE.reportOn(annotations[NativeCoroutinesState])
        }
        //endregion

        //region REDUNDANT_*
        if (isOverride) {
            REDUNDANT_OVERRIDE_COROUTINES.reportOn(annotations[NativeCoroutines])
            REDUNDANT_OVERRIDE_COROUTINES_IGNORE.reportOn(annotations[NativeCoroutinesIgnore])
            REDUNDANT_OVERRIDE_COROUTINES_REFINED.reportOn(annotations[NativeCoroutinesRefined])
            REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE.reportOn(annotations[NativeCoroutinesRefinedState])
            REDUNDANT_OVERRIDE_COROUTINES_STATE.reportOn(annotations[NativeCoroutinesState])
        }
        if (!isPublic) {
            REDUNDANT_PRIVATE_COROUTINES.reportOn(annotations[NativeCoroutines])
            REDUNDANT_PRIVATE_COROUTINES_IGNORE.reportOn(annotations[NativeCoroutinesIgnore])
            REDUNDANT_PRIVATE_COROUTINES_REFINED.reportOn(annotations[NativeCoroutinesRefined])
            REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE.reportOn(annotations[NativeCoroutinesRefinedState])
            REDUNDANT_PRIVATE_COROUTINES_STATE.reportOn(annotations[NativeCoroutinesState])
        }
        //endregion

        //region UNSUPPORTED_*
        if (declaration is FirProperty && declaration.dispatchReceiverType != null && declaration.isExtension) {
            coroutinesAnnotations.forEach { UNSUPPORTED_CLASS_EXTENSION_PROPERTY.reportOn(it) }
        }
        //endregion
    }
}
