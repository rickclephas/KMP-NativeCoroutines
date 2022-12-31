package com.rickclephas.kmp.nativecoroutines.compiler.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.psi.KtElement

internal object KmpNativeCoroutinesErrors {
    @JvmField
    val CONFLICT_COROUTINES_STATE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val EXPOSED_FLOW_TYPE = DiagnosticFactory0.create<PsiElement>(Severity.WARNING)
    @JvmField
    val EXPOSED_FLOW_TYPE_ERROR = DiagnosticFactory0.create<PsiElement>(Severity.ERROR)
    @JvmField
    val EXPOSED_STATE_FLOW_PROPERTY = DiagnosticFactory0.create<PsiElement>(Severity.WARNING)
    @JvmField
    val EXPOSED_STATE_FLOW_PROPERTY_ERROR = DiagnosticFactory0.create<PsiElement>(Severity.ERROR)
    @JvmField
    val EXPOSED_SUSPEND_FUNCTION = DiagnosticFactory0.create<PsiElement>(Severity.WARNING)
    @JvmField
    val EXPOSED_SUSPEND_FUNCTION_ERROR = DiagnosticFactory0.create<PsiElement>(Severity.ERROR)
    @JvmField
    val IGNORED_COROUTINES = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val IGNORED_COROUTINES_STATE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val INVALID_COROUTINES = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val INVALID_COROUTINES_IGNORE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val INVALID_COROUTINES_STATE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val INVALID_COROUTINE_SCOPE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val REDUNDANT_OVERRIDE_COROUTINES = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val REDUNDANT_OVERRIDE_COROUTINES_IGNORE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val REDUNDANT_OVERRIDE_COROUTINES_STATE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val REDUNDANT_PRIVATE_COROUTINES = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val REDUNDANT_PRIVATE_COROUTINES_IGNORE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)
    @JvmField
    val REDUNDANT_PRIVATE_COROUTINES_STATE = DiagnosticFactory0.create<KtElement>(Severity.ERROR)

    init {
        Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(KmpNativeCoroutinesErrors::class.java, DefaultErrorMessages)
    }
}
