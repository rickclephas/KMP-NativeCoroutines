package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.getAllSubstitutedSupertypes
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.typeUtil.supertypes

private val flowFqName = FqName("kotlinx.coroutines.flow.Flow")
private val flowClassId = ClassId.topLevel(flowFqName)

internal fun ModuleDescriptor.findFlowClassifier(): ClassifierDescriptor =
    findClassifierAcrossModuleDependencies(flowClassId)
        ?: throw NoSuchElementException("Couldn't find Flow classifier")

internal val SimpleFunctionDescriptor.hasFlowReturnType: Boolean
    get() {
        val returnType = returnType ?: return false
        val flowTypeConstructor = module.findFlowClassifier().typeConstructor
        return returnType.constructor == flowTypeConstructor || returnType.supertypes().any {
            it.constructor == flowTypeConstructor
        }
    }

internal val PropertyDescriptor.hasFlowType: Boolean
    get() {
        val type = type
        val flowTypeConstructor = module.findFlowClassifier().typeConstructor
        return type.constructor == flowTypeConstructor || type.supertypes().any {
            it.constructor == flowTypeConstructor
        }
    }

internal fun SimpleFunctionDescriptor.getFlowValueTypeOrNull(): TypeProjection? {
    val returnType = returnType ?: return null
    val flowTypeConstructor = module.findFlowClassifier().typeConstructor
    if (returnType.constructor == flowTypeConstructor) {
        return returnType.arguments.first()
    }
    return returnType.supertypes().firstOrNull {
        it.constructor == flowTypeConstructor
    }?.arguments?.first()
}

internal fun PropertyDescriptor.getFlowValueTypeOrNull(): TypeProjection? {
    val type = type
    val flowTypeConstructor = module.findFlowClassifier().typeConstructor
    if (type.constructor == flowTypeConstructor) {
        return type.arguments.first()
    }
    return type.supertypes().firstOrNull {
        it.constructor == flowTypeConstructor
    }?.arguments?.first()
}

internal val IrType.isFlowType: Boolean
    get() {
        if (this !is IrSimpleType) return false
        return classFqName == flowFqName || superTypes().any { it.isFlowType }
    }

internal fun IrType.getFlowValueTypeOrNull(): IrTypeArgument? {
    if (this !is IrSimpleType) return null
    if (classFqName == flowFqName) return arguments.first()
    val irClass = getClass() ?: return null
    val superTypes = getAllSubstitutedSupertypes(irClass)
    return superTypes.firstOrNull { it.classFqName == flowFqName }
        ?.substitute(irClass.typeParameters, arguments.map { it.typeOrNull!! })
        ?.let { it as? IrSimpleType }
        ?.arguments?.first()
}