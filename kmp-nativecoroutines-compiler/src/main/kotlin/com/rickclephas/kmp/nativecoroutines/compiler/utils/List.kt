package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection

private val listClassId = ClassId.topLevel(FqName("kotlin.collections.List"))

internal fun ModuleDescriptor.findListClassifier(): ClassifierDescriptor =
    findClassifierAcrossModuleDependencies(listClassId)
        ?: throw NoSuchElementException("Couldn't find List classifier")

internal fun ModuleDescriptor.createListType(valueType: KotlinType): KotlinType =
    KotlinTypeFactory.simpleType(findListClassifier().defaultType, arguments = listOf(valueType.asTypeProjection()))

internal fun IrPluginContext.referenceListClass(): IrClassSymbol =
    referenceClass(listClassId) ?: throw NoSuchElementException("Couldn't find List symbol")
