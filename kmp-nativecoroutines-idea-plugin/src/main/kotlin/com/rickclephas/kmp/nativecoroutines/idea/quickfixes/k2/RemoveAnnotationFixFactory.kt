package com.rickclephas.kmp.nativecoroutines.idea.quickfixes.k2

import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaCompilerPluginDiagnostic0
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KotlinQuickFixFactory
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.fixes.KtQuickFixesListBuilder
import org.jetbrains.kotlin.idea.inspections.RemoveAnnotationFix
import org.jetbrains.kotlin.psi.KtAnnotationEntry

internal class RemoveAnnotationFixFactory(
    private val diagnosticFactories: Array<out KtDiagnosticFactory0>
) {

    internal companion object {
        fun KtQuickFixesListBuilder.registerRemoveAnnotationFix(
            vararg diagnosticFactories: KtDiagnosticFactory0
        ) {
            val factory = RemoveAnnotationFixFactory(diagnosticFactories)
            registerFactory(factory.intentionBased)
        }
    }

    private val intentionBased = KotlinQuickFixFactory.IntentionBased { diagnostic: KaCompilerPluginDiagnostic0 ->
        if (diagnosticFactories.none { it.name == diagnostic.factoryName }) return@IntentionBased emptyList()
        val annotationEntry = diagnostic.psi as? KtAnnotationEntry ?: return@IntentionBased emptyList()
        val annotationName = annotationEntry.shortName?.identifierOrNullIfSpecial ?: return@IntentionBased emptyList()
        listOf(RemoveAnnotationFix("Remove @$annotationName annotation", annotationEntry).asIntention())
    }
}
