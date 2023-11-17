package com.rickclephas.kmp.nativecoroutines.idea.quickfixes

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.psi.util.findParentOfType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesFqNames
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.idea.quickfix.AddAnnotationFix
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixes
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtModifierListOwner

internal class AddAnnotationFixFactory(
    private val diagnosticFactories: List<DiagnosticFactory0<KtDeclaration>>,
    private val preferredFqName: FqName,
    private val alternativeFqName: FqName?
): KotlinIntentionActionsFactory() {

    internal companion object {
        fun QuickFixes.registerAddAnnotationFix(
            diagnosticFactories: List<DiagnosticFactory0<KtDeclaration>>,
            preferredFqName: FqName,
            alternativeFqName: FqName? = null
        ) {
            val factory = AddAnnotationFixFactory(diagnosticFactories, preferredFqName, alternativeFqName)
            diagnosticFactories.forEach { register(it, factory) }
        }
    }

    private class HighPriorityAddAnnotationFix(
        element: KtModifierListOwner,
        annotationFqName: FqName
    ): AddAnnotationFix(element, ClassId.topLevel(annotationFqName)), HighPriorityAction

    private class LowPriorityAddAnnotationFix(
        element: KtModifierListOwner,
        annotationFqName: FqName
    ): AddAnnotationFix(element, ClassId.topLevel(annotationFqName)), LowPriorityAction

    override fun doCreateActions(diagnostic: Diagnostic): List<IntentionAction> {
        val diagnosticFactory = diagnosticFactories.firstOrNull { it == diagnostic.factory } ?: return emptyList()
        val declaration = when (val element = diagnosticFactory.cast(diagnostic).psiElement) {
            is KtCallableDeclaration -> element
            else -> element.findParentOfType() ?: return emptyList()
        }
        return listOfNotNull(
            HighPriorityAddAnnotationFix(declaration, preferredFqName),
            alternativeFqName?.let { AddAnnotationFix(declaration, ClassId.topLevel(it)) },
            LowPriorityAddAnnotationFix(declaration, NativeCoroutinesFqNames.nativeCoroutinesIgnore)
        )
    }
}
