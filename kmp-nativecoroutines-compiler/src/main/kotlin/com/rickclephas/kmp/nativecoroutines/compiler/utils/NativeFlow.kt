package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.findTypeAliasAcrossModuleDependencies
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory.computeExpandedType
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection

private val typeAliasFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeFlow")
private val typeAliasClassId = ClassId.topLevel(typeAliasFqName)

internal val IrType.isNativeFlow: Boolean
    get() = (this as? IrSimpleType)?.abbreviation?.typeAlias?.owner?.kotlinFqName == typeAliasFqName

internal fun ModuleDescriptor.findNativeFlowTypeAlias(): TypeAliasDescriptor =
    findTypeAliasAcrossModuleDependencies(typeAliasClassId)
        ?: throw NoSuchElementException("Couldn't find NativeFlow typealias")

internal fun ModuleDescriptor.getExpandedNativeFlowType(valueType: KotlinType): KotlinType =
    findNativeFlowTypeAlias().computeExpandedType(listOf(valueType.asTypeProjection()))

private val functionCallableId = CallableId(
    FqName("com.rickclephas.kmp.nativecoroutines"),
    Name.identifier("asNativeFlow")
)

internal fun IrPluginContext.referenceNativeFlowFunction(): IrSimpleFunctionSymbol =
    referenceFunctions(functionCallableId).single()
