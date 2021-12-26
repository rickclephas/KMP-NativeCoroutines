package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.util.findAnnotation
import org.jetbrains.kotlin.name.FqName

private val throwsFqName = FqName("kotlin.Throws")

internal fun List<IrConstructorCall>.findThrowsAnnotation() = findAnnotation(throwsFqName)