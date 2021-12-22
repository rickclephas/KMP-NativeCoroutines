package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.utils.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.descriptors.AbstractLazyMemberScope
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import java.util.ArrayList

internal class KmpNativeCoroutinesSyntheticResolveExtension(
    private val nameGenerator: NameGenerator
): SyntheticResolveExtension {

    // We need the user declared functions. Unfortunately there doesn't seem to be an official way for that.
    // Instead, we'll use reflection to use the same code the compiler is using.
    // https://github.com/JetBrains/kotlin/blob/fe8f7cfcae3b33ba7ee5d06cd45e5e68f3c421a8/compiler/frontend/src/org/jetbrains/kotlin/resolve/lazy/descriptors/LazyClassMemberScope.kt#L64
    @Suppress("UNCHECKED_CAST")
    private fun ClassDescriptor.getDeclarations(): MutableSet<DeclarationDescriptor> {
        val memberScope = unsubstitutedMemberScope
        if (memberScope !is LazyClassMemberScope) return mutableSetOf()
        return AbstractLazyMemberScope::class.java.declaredMethods
            .first { it.name == "computeDescriptorsFromDeclaredElements" }
            .apply { isAccessible = true }
            .invoke(memberScope, DescriptorKindFilter.ALL, MemberScope.ALL_NAME_FILTER,
                NoLookupLocation.WHEN_GET_ALL_DESCRIPTORS) as MutableSet<DeclarationDescriptor>
    }

    // We need two extensions so that we can check for recursion calls with `syntheticResolveExtensionClassName`.
    class RecursiveCallSyntheticResolveExtension : SyntheticResolveExtension

    private val syntheticResolveExtensionClassName =
        "org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension\$Companion\$getInstance\$1"

    // Our SyntheticResolveExtension might be called recursively, either by some other plugin
    // or by our own SyntheticResolveExtension when we try to check some return types.
    // To prevent these loops we'll check the current stacktrace for the `syntheticResolveExtensionClassName`.
    // It should only occur once, if it occurs more than once then this is a recursive call.
    private fun isRecursiveCall(): Boolean {
        Throwable().stackTrace.fold(0) { acc, element ->
            when (element.className) {
                syntheticResolveExtensionClassName -> acc + 1
                else -> acc
            }.also { if (it > 1) return true }
        }.also { if (it < 1) throw IllegalStateException("Not called from SyntheticResolveExtension") }
        return false
    }

    private fun ClassDescriptor.getDeclaredProperties(): List<PropertyDescriptor> =
        getDeclarations().mapNotNull { it as? PropertyDescriptor }

    override fun getSyntheticPropertiesNames(thisDescriptor: ClassDescriptor): List<Name> =
        if (isRecursiveCall()) emptyList()
        else thisDescriptor.getDeclaredProperties()
            .filter { it.needsNativeProperty }
            .flatMap {
                val hasStateFlowType = it.hasStateFlowType
                val hasSharedFlowType = it.hasSharedFlowType
                listOfNotNull(
                    nameGenerator.createNativeName(it.name),
                    if (hasStateFlowType) nameGenerator.createNativeValueName(it.name) else null,
                    if (hasSharedFlowType && !hasStateFlowType) nameGenerator.createNativeReplayCacheName(it.name) else null
                )
            }
            .distinct()

    override fun generateSyntheticProperties(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: ArrayList<PropertyDescriptor>,
        result: MutableSet<PropertyDescriptor>
    ) {
        if (result.isNotEmpty()) return
        val isNativeName = nameGenerator.isNativeName(name)
        val isNativeValueName = nameGenerator.isNativeValueName(name)
        val isNativeReplayCacheName = nameGenerator.isNativeReplayCacheName(name)
        if (!isNativeName && !isNativeValueName && !isNativeReplayCacheName) return
        val originalName = nameGenerator.createOriginalName(name)
        result += thisDescriptor.getDeclaredProperties()
            .filter { it.name == originalName && it.needsNativeProperty }
            .map {
                when {
                    isNativeName -> createNativePropertyDescriptor(thisDescriptor, it, name)
                    isNativeValueName -> createNativeValuePropertyDescriptor(thisDescriptor, it, name)
                    isNativeReplayCacheName -> createNativeReplayCachePropertyDescriptor(thisDescriptor, it, name)
                    else -> throw IllegalStateException("Unsupported property type")
                }
            }
    }

    private fun createNativePropertyDescriptor(
        thisDescriptor: ClassDescriptor,
        coroutinesPropertyDescriptor: PropertyDescriptor,
        name: Name
    ): PropertyDescriptor {
        val valueType = coroutinesPropertyDescriptor.getFlowValueTypeOrNull()?.type
            ?: throw IllegalStateException("Coroutines property doesn't have a value type")
        val type = thisDescriptor.module.getExpandedNativeFlowType(valueType)
        return createPropertyDescriptor(thisDescriptor, coroutinesPropertyDescriptor.visibility,
            name, type, coroutinesPropertyDescriptor.dispatchReceiverParameter,
            coroutinesPropertyDescriptor.extensionReceiverParameter
        )
    }

    private fun createNativeValuePropertyDescriptor(
        thisDescriptor: ClassDescriptor,
        coroutinesPropertyDescriptor: PropertyDescriptor,
        name: Name
    ): PropertyDescriptor {
        val valueType = coroutinesPropertyDescriptor.getStateFlowValueTypeOrNull()?.type
            ?: throw IllegalStateException("Coroutines property doesn't have a value type")
        return createPropertyDescriptor(thisDescriptor, coroutinesPropertyDescriptor.visibility,
            name, valueType, coroutinesPropertyDescriptor.dispatchReceiverParameter,
            coroutinesPropertyDescriptor.extensionReceiverParameter
        )
    }

    private fun createNativeReplayCachePropertyDescriptor(
        thisDescriptor: ClassDescriptor,
        coroutinesPropertyDescriptor: PropertyDescriptor,
        name: Name
    ): PropertyDescriptor {
        val valueType = coroutinesPropertyDescriptor.getSharedFlowValueTypeOrNull()?.type
            ?: throw IllegalStateException("Coroutines property doesn't have a value type")
        val type = thisDescriptor.module.createListType(valueType)
        return createPropertyDescriptor(thisDescriptor, coroutinesPropertyDescriptor.visibility,
            name, type, coroutinesPropertyDescriptor.dispatchReceiverParameter,
            coroutinesPropertyDescriptor.extensionReceiverParameter
        )
    }

    private fun createPropertyDescriptor(
        containingDeclaration: DeclarationDescriptor,
        visibility: DescriptorVisibility,
        name: Name,
        outType: KotlinType,
        dispatchReceiverParameter: ReceiverParameterDescriptor?,
        extensionReceiverParameter: ReceiverParameterDescriptor?
    ): PropertyDescriptor = PropertyDescriptorImpl.create(
        containingDeclaration,
        Annotations.EMPTY,
        Modality.FINAL,
        visibility,
        false,
        name,
        CallableMemberDescriptor.Kind.SYNTHESIZED,
        SourceElement.NO_SOURCE,
        false,
        false,
        false,
        false,
        false,
        false
    ).apply {
        setType(
            outType,
            emptyList(),
            dispatchReceiverParameter,
            extensionReceiverParameter
        )
        initialize(
            PropertyGetterDescriptorImpl(
                this,
                Annotations.EMPTY,
                Modality.FINAL,
                visibility,
                false,
                false,
                false,
                CallableMemberDescriptor.Kind.SYNTHESIZED,
                null,
                SourceElement.NO_SOURCE
            ).apply { initialize(outType) },
            null
        )
    }

    private fun ClassDescriptor.getDeclaredFunctions(): List<SimpleFunctionDescriptor> =
        getDeclarations().mapNotNull { it as? SimpleFunctionDescriptor }

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
        if (isRecursiveCall()) emptyList()
        else thisDescriptor.getDeclaredFunctions()
            .filter { it.needsNativeFunction }
            .map { nameGenerator.createNativeName(it.name) }
            .distinct()

    override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
    ) {
        if (!nameGenerator.isNativeName(name) || result.isNotEmpty()) return
        val originalName = nameGenerator.createOriginalName(name)
        result += thisDescriptor.getDeclaredFunctions()
            .filter { it.name == originalName && it.needsNativeFunction }
            .map { createNativeFunctionDescriptor(thisDescriptor, it, name) }
    }

    private fun createNativeFunctionDescriptor(
        thisDescriptor: ClassDescriptor,
        coroutinesFunctionDescriptor: SimpleFunctionDescriptor,
        name: Name
    ): SimpleFunctionDescriptor =
        SimpleFunctionDescriptorImpl.create(
            thisDescriptor,
            Annotations.EMPTY,
            name,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            SourceElement.NO_SOURCE
        ).apply {
            val typeParameters = coroutinesFunctionDescriptor.typeParameters.copyFor(this)
            val extensionReceiverParameter = coroutinesFunctionDescriptor.extensionReceiverParameter
                ?.copyFor(this, typeParameters)
            val valueParameters = coroutinesFunctionDescriptor.valueParameters
                .copyFor(this, typeParameters)

            var returnType = coroutinesFunctionDescriptor.returnType
                ?: throw IllegalStateException("Coroutines function doesn't have a return type")
            returnType = returnType.replaceFunctionGenerics(coroutinesFunctionDescriptor, typeParameters)

            // Convert Flow types to NativeFlow
            val flowValueType = coroutinesFunctionDescriptor.getFlowValueTypeOrNull()
            if (flowValueType != null)
                returnType = thisDescriptor.module.getExpandedNativeFlowType(flowValueType.type)

            // Convert suspend function to NativeSuspend
            if (coroutinesFunctionDescriptor.isSuspend)
                returnType = thisDescriptor.module.getExpandedNativeSuspendType(returnType)

            initialize(
                extensionReceiverParameter,
                coroutinesFunctionDescriptor.dispatchReceiverParameter,
                typeParameters,
                valueParameters,
                returnType,
                Modality.FINAL,
                coroutinesFunctionDescriptor.visibility
            )
        }
}