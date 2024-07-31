package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.fir.analysis.getChild
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.lexer.KtTokens

// https://github.com/JetBrains/kotlin/blob/fef7e06fe2603d0a2f53994247a4cbd1467457a5/compiler/fir/checkers/src/org/jetbrains/kotlin/fir/analysis/checkers/syntax/FirExplicitApiDeclarationChecker.kt#L144-L158
internal fun FirCallableDeclaration.hasImplicitReturnType(): Boolean {
    val source = source ?: return false
    if (source.getChild(KtNodeTypes.TYPE_REFERENCE, depth = 1) != null) return false
    return when (source.elementType) {
        KtNodeTypes.FUN -> source.getChild(KtTokens.EQ, depth = 1) != null
        KtNodeTypes.PROPERTY -> true
        else -> false
    }
}
