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
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.UNSUPPORTED_CLASS_EXTENSION_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.KmpNativeCoroutinesErrors.UNSUPPORTED_INPUT_FLOW
import com.rickclephas.kmp.nativecoroutines.compiler.classic.utils.NativeCoroutinesAnnotations
import com.rickclephas.kmp.nativecoroutines.compiler.classic.utils.coroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.classic.utils.isCoroutineScopeProperty
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.hasFlow
import com.rickclephas.kmp.nativecoroutines.compiler.utils.hasSuspend
import com.rickclephas.kmp.nativecoroutines.compiler.utils.hasUnsupportedInputFlow
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils.getSourceFromAnnotation
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils.getSourceFromDescriptor
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyPublicApi
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension

public class KmpNativeCoroutinesChecker(
    exposedSeverity: ExposedSeverity
): DeclarationChecker {

    private val exposedSuspendFunction = when (exposedSeverity) {
        ExposedSeverity.NONE -> null
        ExposedSeverity.WARNING -> EXPOSED_SUSPEND_FUNCTION
        ExposedSeverity.ERROR -> EXPOSED_SUSPEND_FUNCTION_ERROR
    }

    private val exposedSuspendType = when (exposedSeverity) {
        ExposedSeverity.NONE -> null
        ExposedSeverity.WARNING -> EXPOSED_SUSPEND_TYPE
        ExposedSeverity.ERROR -> EXPOSED_SUSPEND_TYPE_ERROR
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
        if (descriptor !is CallableDescriptor) return
        if (descriptor !is SimpleFunctionDescriptor && descriptor !is PropertyDescriptor) return
        val annotations = NativeCoroutinesAnnotations(descriptor)
        val isPublic = descriptor.isEffectivelyPublicApi
        val isOverride = descriptor.overriddenDescriptors.isNotEmpty()
        val isSuspend = descriptor is FunctionDescriptor && descriptor.isSuspend
        val coroutineExtensionParameter = descriptor.extensionReceiverParameter?.let { parameter ->
            parameter.coroutinesReturnType?.let { parameter to it }
        }
        val coroutinesValueParameters = descriptor.valueParameters.mapNotNull { parameter ->
            parameter.coroutinesReturnType?.let { parameter to it }
        }
        val returnType = descriptor.coroutinesReturnType

        //region CONFLICT_COROUTINES
        val coroutinesAnnotations = listOf(
            annotations.nativeCoroutines,
            annotations.nativeCoroutinesRefined,
            annotations.nativeCoroutinesRefinedState,
            annotations.nativeCoroutinesState,
        )
        val annotationCount = coroutinesAnnotations.count { it != null }
        if (annotationCount > 1) {
            coroutinesAnnotations.forEach {
                context.trace.report(CONFLICT_COROUTINES, it, declaration)
            }
        }
        //endregion

        //region EXPOSED_*
        if (isPublic && !isOverride && annotationCount == 0 && annotations.nativeCoroutinesIgnore == null) {
            if (isSuspend) exposedSuspendFunction?.on(declaration)?.let(context.trace::report)
            coroutineExtensionParameter?.let { (param, type) ->
                val element = getSourceFromDescriptor(param) as? KtParameter ?: declaration
                if (type.hasSuspend) exposedSuspendType?.on(element)?.let(context.trace::report)
                if (type.hasFlow) exposedFlowType?.on(element)?.let(context.trace::report)
            }
            coroutinesValueParameters.forEach { (param, type) ->
                val element = getSourceFromDescriptor(param) as? KtParameter ?: declaration
                if (type.hasSuspend) exposedSuspendType?.on(element)?.let(context.trace::report)
                if (type.hasFlow) exposedFlowType?.on(element)?.let(context.trace::report)
            }
            if (descriptor is PropertyDescriptor && returnType == CoroutinesReturnType.Flow.State) {
                exposedStateFlowProperty?.on(declaration)?.let(context.trace::report)
            } else if (returnType != null) {
                if (returnType.hasSuspend) exposedSuspendType?.on(declaration)?.let(context.trace::report)
                if (returnType.hasFlow) exposedFlowType?.on(declaration)?.let(context.trace::report)
            }
        }
        //endregion

        //region IGNORED_*
        if (annotations.nativeCoroutinesIgnore != null) {
            context.trace.report(IGNORED_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(IGNORED_COROUTINES_REFINED, annotations.nativeCoroutinesRefined, declaration)
            context.trace.report(IGNORED_COROUTINES_REFINED_STATE, annotations.nativeCoroutinesRefinedState, declaration)
            context.trace.report(IGNORED_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }
        //endregion

        //region INVALID_*
        if (!descriptor.isCoroutineScopeProperty) {
            context.trace.report(INVALID_COROUTINE_SCOPE, annotations.nativeCoroutineScope, declaration)
        }
        if (!isSuspend && coroutineExtensionParameter == null && coroutinesValueParameters.isEmpty() && returnType == null) {
            context.trace.report(INVALID_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(INVALID_COROUTINES_IGNORE, annotations.nativeCoroutinesIgnore, declaration)
            context.trace.report(INVALID_COROUTINES_REFINED, annotations.nativeCoroutinesRefined, declaration)
        }
        if (descriptor !is PropertyDescriptor || returnType !is CoroutinesReturnType.Flow.State) {
            context.trace.report(INVALID_COROUTINES_REFINED_STATE, annotations.nativeCoroutinesRefinedState, declaration)
            context.trace.report(INVALID_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }
        //endregion

        //region REDUNDANT_*
        if (isOverride) {
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_IGNORE, annotations.nativeCoroutinesIgnore, declaration)
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_REFINED, annotations.nativeCoroutinesRefined, declaration)
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE, annotations.nativeCoroutinesRefinedState, declaration)
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }
        if (!isPublic) {
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_IGNORE, annotations.nativeCoroutinesIgnore, declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_REFINED, annotations.nativeCoroutinesRefined, declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE, annotations.nativeCoroutinesRefinedState, declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }
        //endregion

        //region UNSUPPORTED_*
        if (descriptor is PropertyDescriptor && descriptor.dispatchReceiverParameter != null && descriptor.isExtension) {
            coroutinesAnnotations.forEach {
                context.trace.report(UNSUPPORTED_CLASS_EXTENSION_PROPERTY, it, declaration)
            }
        }
        if (
            coroutineExtensionParameter?.second.hasUnsupportedInputFlow(true) ||
            coroutinesValueParameters.any { it.second.hasUnsupportedInputFlow(true) } ||
            returnType.hasUnsupportedInputFlow(false)
        ) {
            coroutinesAnnotations.forEach {
                context.trace.report(UNSUPPORTED_INPUT_FLOW, it, declaration)
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
