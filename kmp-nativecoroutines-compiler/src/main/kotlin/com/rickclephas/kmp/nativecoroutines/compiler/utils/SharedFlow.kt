package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.findClassifierAcrossModuleDependencies
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.TypeProjection

private val sharedFlowFqName = FqName("kotlinx.coroutines.flow.SharedFlow")
private val sharedFlowClassId = ClassId.topLevel(sharedFlowFqName)

private fun ModuleDescriptor.findSharedFlowClassifier(): ClassifierDescriptor =
    findClassifierAcrossModuleDependencies(sharedFlowClassId)
        ?: throw NoSuchElementException("Couldn't find SharedFlow classifier")

internal val PropertyDescriptor.hasSharedFlowType: Boolean
    get() = type.isFlowType(module.findSharedFlowClassifier().typeConstructor)

internal fun PropertyDescriptor.getSharedFlowValueTypeOrNull(): TypeProjection? =
    getFlowValueTypeOrNull(module.findSharedFlowClassifier().typeConstructor)

internal fun IrType.getSharedFlowValueTypeOrNull(): IrTypeArgument? =
    getFlowValueTypeOrNull(sharedFlowFqName)

private val sharedFlowReplayCacheCallableId = CallableId(sharedFlowClassId, Name.identifier("replayCache"))

internal fun IrPluginContext.referenceSharedFlowReplayCacheProperty(): IrPropertySymbol =
    referenceProperties(sharedFlowReplayCacheCallableId).single()
