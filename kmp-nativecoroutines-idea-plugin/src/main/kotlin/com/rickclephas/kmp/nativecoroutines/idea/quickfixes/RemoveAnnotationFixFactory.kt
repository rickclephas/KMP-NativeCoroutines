package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.idea.inspections.RemoveAnnotationFix
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixes
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement

internal class RemoveAnnotationFixFactory(
    private val diagnosticFactories: Array<out DiagnosticFactory0<KtElement>>
): KotlinSingleIntentionActionFactory() {

    internal companion object {
        fun QuickFixes.registerRemoveAnnotationFix(
            vararg diagnosticFactories: DiagnosticFactory0<KtElement>
        ) {
            val factory = RemoveAnnotationFixFactory(diagnosticFactories)
            diagnosticFactories.forEach { register(it, factory) }
        }
    }

    override fun createAction(diagnostic: Diagnostic): IntentionAction? {
        val diagnosticFactory = diagnosticFactories.firstOrNull { it == diagnostic.factory } ?: return null
        val annotationEntry = diagnosticFactory.cast(diagnostic).psiElement as? KtAnnotationEntry ?: return null
        val annotationName = annotationEntry.shortName?.identifierOrNullIfSpecial ?: return null
        return RemoveAnnotationFix("Remove @$annotationName annotation", annotationEntry)
    }
}
