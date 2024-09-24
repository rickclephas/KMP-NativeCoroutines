package com.rickclephas.kmp.nativecoroutines.idea.quickfixes.k2

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.psi.util.findParentOfType
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaCompilerPluginDiagnostic0
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KotlinQuickFixFactory
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KtQuickFixesListBuilder
import org.jetbrains.kotlin.idea.quickfix.AddAnnotationFix
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtModifierListOwner

internal class AddAnnotationFixFactory(
    private val diagnosticFactories: List<KtDiagnosticFactory0>,
    private val preferredAnnotation: NativeCoroutinesAnnotation,
    private val alternativeAnnotation: NativeCoroutinesAnnotation?
) {

    internal companion object {
        fun KtQuickFixesListBuilder.registerAddAnnotationFix(
            diagnosticFactories: List<KtDiagnosticFactory0>,
            preferredAnnotation: NativeCoroutinesAnnotation,
            alternativeAnnotation: NativeCoroutinesAnnotation? = null
        ) {
            val factory = AddAnnotationFixFactory(diagnosticFactories, preferredAnnotation, alternativeAnnotation)
            registerFactory(factory.intentionBased)
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

    private val intentionBased = KotlinQuickFixFactory.IntentionBased { diagnostic: KaCompilerPluginDiagnostic0 ->
        if (diagnosticFactories.none { it.name == diagnostic.factoryName }) return@IntentionBased emptyList()
        val declaration = when (val element = diagnostic.psi) {
            is KtCallableDeclaration -> element
            else -> element.findParentOfType() ?: return@IntentionBased emptyList()
        }
        listOfNotNull(
            HighPriorityAddAnnotationFix(declaration, preferredAnnotation),
            alternativeAnnotation?.let { AddAnnotationFix(declaration, it.classId) },
            LowPriorityAddAnnotationFix(declaration, NativeCoroutinesAnnotation.NativeCoroutinesIgnore)
        )
    }
}
