package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.idea.quickfix.AddAnnotationFix
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixes
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration

internal class AddAnnotationFixFactory(
    private val annotationFqName: FqName,
    private val diagnosticFactories: Array<out DiagnosticFactory0<PsiElement>>
): KotlinSingleIntentionActionFactory() {

    internal companion object {
        fun QuickFixes.registerAddAnnotationFix(
            annotationFqName: FqName,
            vararg diagnosticFactories: DiagnosticFactory0<PsiElement>
        ) {
            val factory = AddAnnotationFixFactory(annotationFqName, diagnosticFactories)
            diagnosticFactories.forEach { register(it, factory) }
        }
    }

    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val diagnosticFactory = diagnosticFactories.firstOrNull { it == diagnostic.factory } ?: return null
        val declaration = when (val element = diagnosticFactory.cast(diagnostic).psiElement) {
            is KtCallableDeclaration -> element
            else -> element.findParentOfType() ?: return null
        }
        return AddAnnotationFix(declaration, annotationFqName)
    }
}
