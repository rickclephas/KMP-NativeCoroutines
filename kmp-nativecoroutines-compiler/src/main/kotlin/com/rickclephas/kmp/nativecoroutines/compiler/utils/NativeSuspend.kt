package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.findTypeAliasAcrossModuleDependencies
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory.computeExpandedType
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection

private val typeAliasFqName = FqName("com.rickclephas.kmp.nativecoroutines.NativeSuspend")
private val typeAliasClassId = ClassId.topLevel(typeAliasFqName)

internal val IrType.isNativeSuspend: Boolean
    get() = (this as? IrSimpleType)?.abbreviation?.typeAlias?.owner?.kotlinFqName == typeAliasFqName

internal fun ModuleDescriptor.findNativeSuspendTypeAlias(): TypeAliasDescriptor =
    findTypeAliasAcrossModuleDependencies(typeAliasClassId)
        ?: throw NoSuchElementException("Couldn't find NativeSuspend typealias")

internal fun ModuleDescriptor.getExpandedNativeSuspendType(valueType: KotlinType): KotlinType =
    findNativeSuspendTypeAlias().computeExpandedType(listOf(valueType.asTypeProjection()))

private val functionFqName = FqName("com.rickclephas.kmp.nativecoroutines.nativeSuspend")

internal fun IrPluginContext.referenceNativeSuspendFunction(): IrSimpleFunctionSymbol =
    referenceFunctions(functionFqName).single()