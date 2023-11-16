package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.TypeConstructor

private val coroutineScopeFqName = FqName("kotlinx.coroutines.CoroutineScope")
internal val coroutineScopeClassId = ClassId.topLevel(coroutineScopeFqName)

internal fun ModuleDescriptor.findCoroutineScopeConstructor(): TypeConstructor =
    findClassifierAcrossModuleDependencies(coroutineScopeClassId)?.typeConstructor
        ?: throw NoSuchElementException("Couldn't find CoroutineScope constructor")
