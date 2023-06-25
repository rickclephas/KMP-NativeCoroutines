package com.rickclephas.kmp.nativecoroutines.compiler.diagnostics

import com.intellij.psi.PsiElement
import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.config.exposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.CONFLICT_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.IGNORED_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.INVALID_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_STATE
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY
import com.rickclephas.kmp.nativecoroutines.compiler.diagnostics.KmpNativeCoroutinesErrors.EXPOSED_STATE_FLOW_PROPERTY_ERROR
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotations
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.coroutinesReturnType
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils.getSourceFromAnnotation
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyPublicApi

internal class KmpNativeCoroutinesChecker(
    compilerConfiguration: CompilerConfiguration
): DeclarationChecker {

    private val exposedSeverity: ExposedSeverity = compilerConfiguration.exposedSeverity

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
        val returnType = descriptor.coroutinesReturnType

        if (annotations.nativeCoroutines != null && annotations.nativeCoroutinesState != null) {
            context.trace.report(CONFLICT_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }

        val exposedSuspendFunction: DiagnosticFactory0<PsiElement>?
        val exposedFlowType: DiagnosticFactory0<PsiElement>?
        val exposedStateFlowProperty: DiagnosticFactory0<PsiElement>?
        when (exposedSeverity) {
            ExposedSeverity.NONE -> {
                exposedSuspendFunction = null
                exposedFlowType = null
                exposedStateFlowProperty = null
            }
            ExposedSeverity.WARNING -> {
                exposedSuspendFunction = EXPOSED_SUSPEND_FUNCTION
                exposedFlowType = EXPOSED_FLOW_TYPE
                exposedStateFlowProperty = EXPOSED_STATE_FLOW_PROPERTY
            }
            ExposedSeverity.ERROR -> {
                exposedSuspendFunction = EXPOSED_SUSPEND_FUNCTION_ERROR
                exposedFlowType = EXPOSED_FLOW_TYPE_ERROR
                exposedStateFlowProperty = EXPOSED_STATE_FLOW_PROPERTY_ERROR
            }
        }
        if (isPublic && !isOverride && annotations.nativeCoroutines == null &&
            annotations.nativeCoroutinesIgnore == null && annotations.nativeCoroutinesState == null
        ) {
            if (isSuspend) {
                val element = declaration.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: declaration
                exposedSuspendFunction?.on(element)?.let(context.trace::report)
            }
            if (returnType is CoroutinesReturnType.Flow) {
                val diagnosticFactory = when {
                    descriptor !is PropertyDescriptor -> exposedFlowType
                    returnType != CoroutinesReturnType.Flow.State -> exposedFlowType
                    else -> exposedStateFlowProperty
                }
                val element = (declaration as? KtCallableDeclaration)?.let {
                    it.typeReference ?: it.nameIdentifier
                } ?: declaration
                diagnosticFactory?.on(element)?.let(context.trace::report)
            }
        }

        if (annotations.nativeCoroutinesIgnore != null) {
            context.trace.report(IGNORED_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(IGNORED_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }

        if (descriptor !is PropertyDescriptor || returnType != CoroutinesReturnType.CoroutineScope) {
            context.trace.report(INVALID_COROUTINE_SCOPE, annotations.nativeCoroutineScope, declaration)
        }
        if (!isSuspend && returnType !is CoroutinesReturnType.Flow) {
            context.trace.report(INVALID_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(INVALID_COROUTINES_IGNORE, annotations.nativeCoroutinesIgnore, declaration)
        }
        if (descriptor !is PropertyDescriptor || returnType !is CoroutinesReturnType.Flow.State) {
            context.trace.report(INVALID_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }

        if (isOverride) {
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_IGNORE, annotations.nativeCoroutinesIgnore, declaration)
            context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }
        if (!isPublic) {
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES, annotations.nativeCoroutines, declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_IGNORE, annotations.nativeCoroutinesIgnore, declaration)
            context.trace.report(REDUNDANT_PRIVATE_COROUTINES_STATE, annotations.nativeCoroutinesState, declaration)
        }
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
