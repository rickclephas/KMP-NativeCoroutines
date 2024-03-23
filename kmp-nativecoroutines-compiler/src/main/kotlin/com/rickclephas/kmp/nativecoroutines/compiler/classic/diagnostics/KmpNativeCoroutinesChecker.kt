package com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.CONFLICT_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_ACTUAL_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.UNSUPPORTED_CLASS_EXTENSION_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.utils.coroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.classic.utils.getNativeCoroutinesAnnotations
import com.rickclephas.kmp.nativecoroutines.compiler.classic.utils.isRefined
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesState
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils.getSourceFromAnnotation
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyPublicApi
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.multiplatform.findExpects
import java.nio.file.Path
import kotlin.io.path.Path

public class KmpNativeCoroutinesChecker(
    exposedSeverity: ExposedSeverity,
    private val generatedSourceDirs: List<Path>
): DeclarationChecker {

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

    override fun check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
    ) {
        if (descriptor !is CallableMemberDescriptor) return
        if (descriptor !is SimpleFunctionDescriptor && descriptor !is PropertyDescriptor) return
        val annotations = descriptor.getNativeCoroutinesAnnotations()
        val isRefined = descriptor.isRefined
        val isPublic = descriptor.isEffectivelyPublicApi
        val isOverride = descriptor.overriddenDescriptors.isNotEmpty()
        val isActual = descriptor.isActual
        val isSuspend = descriptor is FunctionDescriptor && descriptor.isSuspend
        val returnType = descriptor.coroutinesReturnType

        //region CONFLICT_COROUTINES
        val coroutinesAnnotations = listOfNotNull(
            annotations[NativeCoroutines],
            annotations[NativeCoroutinesRefined],
            annotations[NativeCoroutinesRefinedState],
            annotations[NativeCoroutinesState],
        )
        if (coroutinesAnnotations.size > 1) {
            coroutinesAnnotations.forEach {
                context.trace.report(CONFLICT_COROUTINES, it, declaration)
            }
        }
        //endregion

        //region EXPOSED_*
        val hasAnnotation = coroutinesAnnotations.isNotEmpty()
        val isIgnored = annotations.contains(NativeCoroutinesIgnore)
        if (!isRefined && isPublic && !isOverride && !isActual && !hasAnnotation && !isIgnored) {
            val isGenerated = generatedSourceDirs.any(Path(declaration.containingKtFile.virtualFilePath)::startsWith)
            if (!isGenerated && isSuspend) {
                exposedSuspendFunction?.on(declaration)?.let(context.trace::report)
            }
            if (!isGenerated && returnType is CoroutinesReturnType.Flow) {
                val diagnosticFactory = when {
                    descriptor !is PropertyDescriptor -> exposedFlowType
                    returnType != CoroutinesReturnType.Flow.State -> exposedFlowType
                    else -> exposedStateFlowProperty
                }
                diagnosticFactory?.on(declaration)?.let(context.trace::report)
            }
        }
        //endregion

        //region IGNORED_*
        if (isIgnored) {
            context.trace.report(IGNORED_COROUTINES, annotations[NativeCoroutines], declaration)
            context.trace.report(IGNORED_COROUTINES_REFINED, annotations[NativeCoroutinesRefined], declaration)
            context.trace.report(IGNORED_COROUTINES_REFINED_STATE, annotations[NativeCoroutinesRefinedState], declaration)
            context.trace.report(IGNORED_COROUTINES_STATE, annotations[NativeCoroutinesState], declaration)
        }
        //endregion

        //region INVALID_*
        if (descriptor !is PropertyDescriptor || returnType != CoroutinesReturnType.CoroutineScope) {
            context.trace.report(INVALID_COROUTINE_SCOPE, annotations[NativeCoroutineScope], declaration)
        }
        if (!isSuspend && returnType !is CoroutinesReturnType.Flow) {
            context.trace.report(INVALID_COROUTINES, annotations[NativeCoroutines], declaration)
            context.trace.report(INVALID_COROUTINES_IGNORE, annotations[NativeCoroutinesIgnore], declaration)
            context.trace.report(INVALID_COROUTINES_REFINED, annotations[NativeCoroutinesRefined], declaration)
        }
        if (descriptor !is PropertyDescriptor || returnType !is CoroutinesReturnType.Flow.State) {
            context.trace.report(INVALID_COROUTINES_REFINED_STATE, annotations[NativeCoroutinesRefinedState], declaration)
            context.trace.report(INVALID_COROUTINES_STATE, annotations[NativeCoroutinesState], declaration)
        }
        //endregion

        //region INCOMPATIBLE_*
        if (isOverride) {
            val overriddenAnnotations = descriptor.overriddenDescriptors.map(Annotated::getNativeCoroutinesAnnotations)
            val invalidAnnotations = annotations.filterKeys { annotation ->
                overriddenAnnotations.any { !it.containsKey(annotation) }
            }
            context.trace.report(INCOMPATIBLE_OVERRIDE_COROUTINES, invalidAnnotations[NativeCoroutines], declaration)
            context.trace.report(INCOMPATIBLE_OVERRIDE_COROUTINES_IGNORE, invalidAnnotations[NativeCoroutinesIgnore], declaration)
            context.trace.report(INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED, invalidAnnotations[NativeCoroutinesRefined], declaration)
            context.trace.report(INCOMPATIBLE_OVERRIDE_COROUTINES_REFINED_STATE, invalidAnnotations[NativeCoroutinesRefinedState], declaration)
            context.trace.report(INCOMPATIBLE_OVERRIDE_COROUTINES_STATE, invalidAnnotations[NativeCoroutinesState], declaration)
        }
        if (isActual) {
            val expect = descriptor.findExpects().singleOrNull() as? CallableMemberDescriptor
            if (expect != null) {
                val expectAnnotations = expect.getNativeCoroutinesAnnotations()
                val invalidAnnotations = annotations.filterKeys { !expectAnnotations.containsKey(it) }
                context.trace.report(INCOMPATIBLE_ACTUAL_COROUTINES, invalidAnnotations[NativeCoroutines], declaration)
                context.trace.report(INCOMPATIBLE_ACTUAL_COROUTINES_IGNORE, invalidAnnotations[NativeCoroutinesIgnore], declaration)
                context.trace.report(INCOMPATIBLE_ACTUAL_COROUTINES_REFINED, invalidAnnotations[NativeCoroutinesRefined], declaration)
                context.trace.report(INCOMPATIBLE_ACTUAL_COROUTINES_REFINED_STATE, invalidAnnotations[NativeCoroutinesRefinedState], declaration)
                context.trace.report(INCOMPATIBLE_ACTUAL_COROUTINES_STATE, invalidAnnotations[NativeCoroutinesState], declaration)
            }
        }
        //endregion

        //region REDUNDANT_*
        if (!isOverride && !isPublic) {
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES, annotations[NativeCoroutines], declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_IGNORE, annotations[NativeCoroutinesIgnore], declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_REFINED, annotations[NativeCoroutinesRefined], declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE, annotations[NativeCoroutinesRefinedState], declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_STATE, annotations[NativeCoroutinesState], declaration)
        }
        //endregion

        //region UNSUPPORTED_*
        if (descriptor is PropertyDescriptor && descriptor.dispatchReceiverParameter != null && descriptor.isExtension) {
            coroutinesAnnotations.forEach {
                context.trace.report(UNSUPPORTED_CLASS_EXTENSION_PROPERTY, it, declaration)
            }
        }
        //endregion
    }

    private fun BindingTrace.report(
        diagnosticFactory: DiagnosticFactory0<KtElement>,
        annotationDescriptor: AnnotationDescriptor?,
        declaration: KtDeclaration
    ) {
        if (annotationDescriptor == null) return
        report(diagnosticFactory.on(getSourceFromAnnotation(annotationDescriptor) ?: declaration))
    }
}
