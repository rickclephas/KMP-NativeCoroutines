package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.TypeConstructor

private val flowFqName = FqName("kotlinx.coroutines.flow.Flow")
private val flowClassId = ClassId.topLevel(flowFqName)

internal fun ModuleDescriptor.findFlowConstructor(): TypeConstructor =
    findClassifierAcrossModuleDependencies(flowClassId)?.typeConstructor
        ?: throw NoSuchElementException("Couldn't find Flow constructor")
