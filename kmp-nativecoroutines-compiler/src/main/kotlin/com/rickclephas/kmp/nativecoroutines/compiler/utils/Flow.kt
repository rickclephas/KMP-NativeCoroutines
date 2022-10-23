package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.typeUtil.supertypes

private val flowFqName = FqName("kotlinx.coroutines.flow.Flow")
private val flowClassId = ClassId.topLevel(flowFqName)

private fun ModuleDescriptor.findFlowClassifier(): ClassifierDescriptor =
    findClassifierAcrossModuleDependencies(flowClassId)
        ?: throw NoSuchElementException("Couldn't find Flow classifier")

private fun KotlinType.isFlowType(typeConstructor: TypeConstructor): Boolean =
    constructor == typeConstructor || supertypes().any {
        it.constructor == typeConstructor
    }

internal val SimpleFunctionDescriptor.hasFlowReturnType: Boolean
    get() = returnType?.isFlowType(module.findFlowClassifier().typeConstructor) ?: false

internal val PropertyDescriptor.hasFlowType: Boolean
    get() = type.isFlowType(module.findFlowClassifier().typeConstructor)
