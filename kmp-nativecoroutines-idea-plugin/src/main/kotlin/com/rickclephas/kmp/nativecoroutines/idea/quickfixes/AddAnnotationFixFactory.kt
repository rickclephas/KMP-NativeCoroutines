package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.idea.quickfix.AddAnnotationFix
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixes
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration

internal class AddAnnotationFixFactory(
    private val diagnosticFactories: List<DiagnosticFactory0<PsiElement>>,
    private val annotationFqNames: List<FqName>
): KotlinIntentionActionsFactory() {

    internal companion object {
        fun QuickFixes.registerAddAnnotationFix(
            diagnosticFactories: List<DiagnosticFactory0<PsiElement>>,
            annotationFqNames: List<FqName>
        ) {
            val factory = AddAnnotationFixFactory(diagnosticFactories, annotationFqNames)
            diagnosticFactories.forEach { register(it, factory) }
        }
    }

    override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
        val diagnosticFactory = diagnosticFactories.firstOrNull { it == diagnostic.factory } ?: return emptyList()
        val declaration = when (val element = diagnosticFactory.cast(diagnostic).psiElement) {
            is KtCallableDeclaration -> element
            else -> element.findParentOfType() ?: return emptyList()
        }
        return annotationFqNames.map { AddAnnotationFix(declaration, it) }
    }
}
