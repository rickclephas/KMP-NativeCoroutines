package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.TypeProjection

private val stateFlowFqName = FqName("kotlinx.coroutines.flow.StateFlow")
private val stateFlowClassId = ClassId.topLevel(stateFlowFqName)

private fun ModuleDescriptor.findStateFlowClassifier(): ClassifierDescriptor =
    findClassifierAcrossModuleDependencies(stateFlowClassId)
        ?: throw NoSuchElementException("Couldn't find StateFlow classifier")

internal val PropertyDescriptor.hasStateFlowType: Boolean
    get() = type.isFlowType(module.findStateFlowClassifier().typeConstructor)

internal fun PropertyDescriptor.getStateFlowValueTypeOrNull(): TypeProjection? =
    getFlowValueTypeOrNull(module.findStateFlowClassifier().typeConstructor)

internal fun IrType.getStateFlowValueTypeOrNull(): IrTypeArgument? =
    getFlowValueTypeOrNull(stateFlowFqName)

private val stateFlowValueCallableId = CallableId(stateFlowClassId, Name.identifier("value"))

internal fun IrPluginContext.referenceStateFlowValueProperty(): IrPropertySymbol =
    referenceProperties(stateFlowValueCallableId).single()
