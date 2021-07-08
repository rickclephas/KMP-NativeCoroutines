package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.getAllSubstitutedSupertypes
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.typeUtil.supertypes

private val flowFqName = FqName("kotlinx.coroutines.flow.Flow")
private val flowClassId = ClassId.topLevel(flowFqName)

private fun ModuleDescriptor.findFlowClassifier(): ClassifierDescriptor =
    findClassifierAcrossModuleDependencies(flowClassId)
        ?: throw NoSuchElementException("Couldn't find Flow classifier")

internal fun KotlinType.isFlowType(typeConstructor: TypeConstructor): Boolean =
    constructor == typeConstructor || supertypes().any {
        it.constructor == typeConstructor
    }

internal val SimpleFunctionDescriptor.hasFlowReturnType: Boolean
    get() = returnType?.isFlowType(module.findFlowClassifier().typeConstructor) ?: false

internal val PropertyDescriptor.hasFlowType: Boolean
    get() = type.isFlowType(module.findFlowClassifier().typeConstructor)

private fun KotlinType.getFlowValueTypeOrNull(typeConstructor: TypeConstructor): TypeProjection? {
    if (constructor == typeConstructor) {
        return arguments.first()
    }
    return supertypes().firstOrNull {
        it.constructor == typeConstructor
    }?.arguments?.first()
}

internal fun SimpleFunctionDescriptor.getFlowValueTypeOrNull(
    typeConstructor: TypeConstructor = module.findFlowClassifier().typeConstructor
) = returnType?.getFlowValueTypeOrNull(typeConstructor)

internal fun PropertyDescriptor.getFlowValueTypeOrNull(
    typeConstructor: TypeConstructor = module.findFlowClassifier().typeConstructor
) = type.getFlowValueTypeOrNull(typeConstructor)

internal val IrType.isFlowType: Boolean
    get() {
        if (this !is IrSimpleType) return false
        return classFqName == flowFqName || superTypes().any { it.isFlowType }
    }

internal fun IrType.getFlowValueTypeOrNull(fqName: FqName = flowFqName): IrTypeArgument? {
    if (this !is IrSimpleType) return null
    if (classFqName == fqName) return arguments.first()
    val irClass = getClass() ?: return null
    val superTypes = getAllSubstitutedSupertypes(irClass)
    return superTypes.firstOrNull { it.classFqName == fqName }
        ?.substitute(irClass.typeParameters, arguments.map { it.typeOrNull!! })
        ?.let { it as? IrSimpleType }
        ?.arguments?.first()
}