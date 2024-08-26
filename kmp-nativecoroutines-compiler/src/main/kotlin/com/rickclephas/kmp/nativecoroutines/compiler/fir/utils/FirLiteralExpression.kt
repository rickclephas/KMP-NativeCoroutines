package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.fir.expressions.FirLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.types.ConstantValueKind

internal fun String.asFirExpression(): FirLiteralExpression =
    buildLiteralExpression(null, ConstantValueKind.String, this, setType = true)
