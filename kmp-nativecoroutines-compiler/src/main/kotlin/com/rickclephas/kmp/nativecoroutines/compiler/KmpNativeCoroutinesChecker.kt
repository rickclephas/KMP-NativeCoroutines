package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.EXPOSED_FLOW_TYPE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.EXPOSED_SUSPEND_FUNCTION
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.IGNORED_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.INVALID_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.INVALID_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.INVALID_COROUTINE_SCOPE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_OVERRIDE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES
import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesErrors.REDUNDANT_PRIVATE_COROUTINES_IGNORE
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotations
import com.rickclephas.kmp.nativecoroutines.compiler.utils.CoroutinesReturnType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.coroutinesReturnType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils.getSourceFromAnnotation
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isEffectivelyPublicApi

internal object KmpNativeCoroutinesChecker: DeclarationChecker {

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

        if (isPublic && !isOverride && annotations.nativeCoroutines == null && annotations.nativeCoroutinesIgnore == null) {
            if (isSuspend) context.trace.report(EXPOSED_SUSPEND_FUNCTION.on(declaration))
            if (returnType == CoroutinesReturnType.FLOW) context.trace.report(EXPOSED_FLOW_TYPE.on(declaration))
        }

        if (!isSuspend && returnType != CoroutinesReturnType.FLOW) {
            if (annotations.nativeCoroutines != null) {
                val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutines) ?: declaration
                context.trace.report(INVALID_COROUTINES.on(reportLocation))
            }
            if (annotations.nativeCoroutinesIgnore != null) {
                val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutinesIgnore) ?: declaration
                context.trace.report(INVALID_COROUTINES_IGNORE.on(reportLocation))
            }
        } else if (isOverride) {
            if (annotations.nativeCoroutines != null) {
                val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutines) ?: declaration
                context.trace.report(REDUNDANT_OVERRIDE_COROUTINES.on(reportLocation))
            }
            if (annotations.nativeCoroutinesIgnore != null) {
                val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutinesIgnore) ?: declaration
                context.trace.report(REDUNDANT_OVERRIDE_COROUTINES_IGNORE.on(reportLocation))
            }
        } else if (!isPublic) {
            if (annotations.nativeCoroutines != null) {
                val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutines) ?: declaration
                context.trace.report(REDUNDANT_PRIVATE_COROUTINES.on(reportLocation))
            }
            if (annotations.nativeCoroutinesIgnore != null) {
                val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutinesIgnore) ?: declaration
                context.trace.report(REDUNDANT_PRIVATE_COROUTINES_IGNORE.on(reportLocation))
            }
        }

        if (annotations.nativeCoroutines != null && annotations.nativeCoroutinesIgnore != null) {
            val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutines) ?: declaration
                context.trace.report(IGNORED_COROUTINES.on(reportLocation))
        }

        if (annotations.nativeCoroutineScope != null && returnType != CoroutinesReturnType.COROUTINE_SCOPE) {
            val reportLocation = getSourceFromAnnotation(annotations.nativeCoroutineScope) ?: declaration
                context.trace.report(INVALID_COROUTINE_SCOPE.on(reportLocation))
        }
    }
}
