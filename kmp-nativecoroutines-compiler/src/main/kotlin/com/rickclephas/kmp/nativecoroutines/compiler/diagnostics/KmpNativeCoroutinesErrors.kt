package com.rickclephas.kmp.nativecoroutines.compiler.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.psi.KtElement

public object KmpNativeCoroutinesErrors {
    @JvmField
    public val CONFLICT_COROUTINES: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val EXPOSED_FLOW_TYPE: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public val EXPOSED_FLOW_TYPE_ERROR: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val EXPOSED_STATE_FLOW_PROPERTY: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public val EXPOSED_STATE_FLOW_PROPERTY_ERROR: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val EXPOSED_SUSPEND_FUNCTION: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public val EXPOSED_SUSPEND_FUNCTION_ERROR: DiagnosticFactory0<PsiElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val IGNORED_COROUTINES: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val IGNORED_COROUTINES_REFINED: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val IGNORED_COROUTINES_REFINED_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val IGNORED_COROUTINES_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val INVALID_COROUTINES: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val INVALID_COROUTINES_IGNORE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val INVALID_COROUTINES_REFINED: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val INVALID_COROUTINES_REFINED_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val INVALID_COROUTINES_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val INVALID_COROUTINE_SCOPE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val REDUNDANT_OVERRIDE_COROUTINES: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public val REDUNDANT_OVERRIDE_COROUTINES_IGNORE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val REDUNDANT_OVERRIDE_COROUTINES_REFINED: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public val REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public val REDUNDANT_OVERRIDE_COROUTINES_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.WARNING)
    @JvmField
    public  val REDUNDANT_PRIVATE_COROUTINES: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val REDUNDANT_PRIVATE_COROUTINES_IGNORE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val REDUNDANT_PRIVATE_COROUTINES_REFINED: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val REDUNDANT_PRIVATE_COROUTINES_STATE: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)
    @JvmField
    public val UNSUPPORTED_CLASS_EXTENSION_PROPERTY: DiagnosticFactory0<KtElement> = DiagnosticFactory0.create(Severity.ERROR)

    init {
        Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(KmpNativeCoroutinesErrors::class.java, DefaultErrorMessages)
    }
}
