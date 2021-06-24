package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.descriptors.AbstractLazyMemberScope
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinTypeFactory.computeExpandedType
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection

internal class KmpNativeCoroutinesSyntheticResolveExtension(
    private val suffix: String
): SyntheticResolveExtension {

    // We need the user declared functions. Unfortunately there doesn't seem to be an official way for that.
    // Instead we'll use reflection to use the same code the compiler is using.
    // https://github.com/JetBrains/kotlin/blob/fe8f7cfcae3b33ba7ee5d06cd45e5e68f3c421a8/compiler/frontend/src/org/jetbrains/kotlin/resolve/lazy/descriptors/LazyClassMemberScope.kt#L64
    @Suppress("UNCHECKED_CAST")
    private fun getDeclarations(memberScope: MemberScope): List<DeclarationDescriptor> =
        AbstractLazyMemberScope::class.java.declaredMethods
            .first { it.name == "computeDescriptorsFromDeclaredElements" }
            .apply { isAccessible = true }
            .invoke(memberScope, DescriptorKindFilter.ALL, MemberScope.ALL_NAME_FILTER,
                NoLookupLocation.WHEN_GET_ALL_DESCRIPTORS) as List<DeclarationDescriptor>

    private fun getDeclaredFunctions(memberScope: MemberScope): List<SimpleFunctionDescriptor> =
        getDeclarations(memberScope).mapNotNull { it as? SimpleFunctionDescriptor }

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        val suspendFunctions = getDeclaredFunctions(thisDescriptor.unsubstitutedMemberScope).filter {
            !it.name.isSpecial && it.isSuspend
        }
        return suspendFunctions.map {
            Name.identifier("${it.name.identifier}$suffix")
        }
    }

    override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
    ) {
        if (result.isNotEmpty() || name.isSpecial) return
        val identifier = name.identifier
        if (!identifier.endsWith(suffix)) return

        val originalFunctionName = name.identifier.removeSuffix(suffix)
        // TODO: Support function overloads
        val function = getDeclaredFunctions(thisDescriptor.unsubstitutedMemberScope).firstOrNull {
            !it.name.isSpecial && it.isSuspend && it.name.identifier == originalFunctionName
        } ?: return

        result += createNativeSuspendFunctionDescriptor(thisDescriptor, function)
    }

    private fun createNativeSuspendFunctionDescriptor(
        thisDescriptor: ClassDescriptor,
        suspendFunctionDescriptor: SimpleFunctionDescriptor
    ): SimpleFunctionDescriptor {
        val returnType = suspendFunctionDescriptor.returnType
            ?: throw IllegalStateException("Suspend function doesn't have return type")
        val nativeSuspendDescriptor = thisDescriptor.module.findTypeAliasAcrossModuleDependencies(nativeSuspendClassId)
            ?: throw IllegalStateException("Couldn't find NativeSuspend typealias")
        val nativeReturnType = nativeSuspendDescriptor.computeExpandedType(listOf(returnType.asTypeProjection()))

        val name = suspendFunctionDescriptor.name
        if (name.isSpecial) throw IllegalStateException("Suspend function shouldn't have a special name")
        val nativeName = Name.identifier("${name.identifier}$suffix")

        return SimpleFunctionDescriptorImpl.create(
            thisDescriptor,
            Annotations.EMPTY,
            nativeName,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            SourceElement.NO_SOURCE
        ).apply {
            initialize(
                suspendFunctionDescriptor.extensionReceiverParameter,
                suspendFunctionDescriptor.dispatchReceiverParameter,
                emptyList(),
                suspendFunctionDescriptor.valueParameters.map {
                    it.copy(this, it.name, it.index)
                },
                nativeReturnType,
                Modality.FINAL,
                suspendFunctionDescriptor.visibility
            )
        }
    }
}