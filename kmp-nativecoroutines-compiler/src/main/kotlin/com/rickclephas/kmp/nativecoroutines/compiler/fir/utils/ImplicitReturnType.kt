package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.KtRealSourceElementKind
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.expressions.impl.FirSingleExpressionBlock

// https://github.com/JetBrains/kotlin/blob/da6501a98a8f85dd3e7d94dcc17e050b3fefaaa4/compiler/fir/checkers/src/org/jetbrains/kotlin/fir/analysis/checkers/syntax/FirExplicitApiDeclarationChecker.kt#L150C9-L159
internal fun FirCallableDeclaration.hasImplicitReturnType(): Boolean {
    if (source?.kind != KtRealSourceElementKind) return false
    // It's an explicit type, the check always should be skipped
    if (returnTypeRef.source?.kind == KtRealSourceElementKind) return false

    return this is FirProperty ||
            this is FirFunction &&
            // It's allowed to have implicit return type for getters, for setters the return type is always `Unit`.
            // The return type of the outer property is only worth considering.
            this !is FirPropertyAccessor &&
            // Implicit return type can exist only for single-expression functions, unspecified type for regular functions is incorrect.
            body is FirSingleExpressionBlock
}
