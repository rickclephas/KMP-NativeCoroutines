package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.psi.util.findParentOfType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.idea.quickfix.AddAnnotationFix
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixes
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtModifierListOwner

internal class AddAnnotationFixFactory(
    private val diagnosticFactories: List<DiagnosticFactory0<KtDeclaration>>,
    private val preferredAnnotation: NativeCoroutinesAnnotation,
    private val alternativeAnnotation: NativeCoroutinesAnnotation?
): KotlinIntentionActionsFactory() {

    internal companion object {
        fun QuickFixes.registerAddAnnotationFix(
            diagnosticFactories: List<DiagnosticFactory0<KtDeclaration>>,
            preferredAnnotation: NativeCoroutinesAnnotation,
            alternativeAnnotation: NativeCoroutinesAnnotation? = null
        ) {
            val factory = AddAnnotationFixFactory(diagnosticFactories, preferredAnnotation, alternativeAnnotation)
            diagnosticFactories.forEach { register(it, factory) }
        }
    }

    private class HighPriorityAddAnnotationFix(
        element: KtModifierListOwner,
        annotation: NativeCoroutinesAnnotation
    ): AddAnnotationFix(element, annotation.classId), HighPriorityAction

    private class LowPriorityAddAnnotationFix(
        element: KtModifierListOwner,
        annotation: NativeCoroutinesAnnotation
    ): AddAnnotationFix(element, annotation.classId), LowPriorityAction

    override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
        val diagnosticFactory = diagnosticFactories.firstOrNull { it == diagnostic.factory } ?: return emptyList()
        val declaration = when (val element = diagnosticFactory.cast(diagnostic).psiElement) {
            is KtCallableDeclaration -> element
            else -> element.findParentOfType() ?: return emptyList()
        }
        return listOfNotNull(
            HighPriorityAddAnnotationFix(declaration, preferredAnnotation),
            alternativeAnnotation?.let { AddAnnotationFix(declaration, it.classId) },
            LowPriorityAddAnnotationFix(declaration, NativeCoroutinesAnnotation.NativeCoroutinesIgnore)
        )
    }
}
