package com.rickclephas.kmp.nativecoroutines.compiler.classic.utils

import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction

// https://github.com/JetBrains/kotlin/blob/fef7e06fe2603d0a2f53994247a4cbd1467457a5/compiler/frontend/src/org/jetbrains/kotlin/resolve/checkers/ExplicitApiDeclarationChecker.kt#L125-L134
internal fun KtCallableDeclaration.hasImplicitReturnType(): Boolean {
    if (typeReference != null) return false
    if (this is KtNamedFunction && hasBlockBody()) return false
    return true
}
