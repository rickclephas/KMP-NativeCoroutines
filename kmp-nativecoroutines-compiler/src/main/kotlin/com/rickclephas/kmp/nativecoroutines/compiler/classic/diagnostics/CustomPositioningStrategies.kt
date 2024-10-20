package com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics

import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.diagnostics.PositioningStrategies
import org.jetbrains.kotlin.diagnostics.PositioningStrategy
import org.jetbrains.kotlin.diagnostics.hasSyntaxErrors
import org.jetbrains.kotlin.diagnostics.markElement
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement

internal object CustomPositioningStrategies {

    val DECLARATION_RETURN_TYPE = object : PositioningStrategy<KtElement>() {
        override fun mark(element: KtElement): List<TextRange> {
            if (element is KtDeclaration) return PositioningStrategies.DECLARATION_RETURN_TYPE.mark(element)
            return markElement(element)
        }

        override fun isValid(element: KtElement): Boolean {
            if (element is KtDeclaration) return PositioningStrategies.DECLARATION_RETURN_TYPE.isValid(element)
            return !hasSyntaxErrors(element)
        }
    }
}
