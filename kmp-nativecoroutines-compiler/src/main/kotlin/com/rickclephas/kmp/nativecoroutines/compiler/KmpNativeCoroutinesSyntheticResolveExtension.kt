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

    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        val declarations = getDeclarations(thisDescriptor.unsubstitutedMemberScope)
        val suspendFunctions = declarations.mapNotNull { it as? SimpleFunctionDescriptor }
            .filter { !it.name.isSpecial && it.isSuspend }

        return suspendFunctions.map { Name.identifier("${it.name.identifier}$suffix") }
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
        val declarations = getDeclarations(thisDescriptor.unsubstitutedMemberScope)
        val function = declarations.firstOrNull {
            it is SimpleFunctionDescriptor && it.isSuspend && !it.name.isSpecial &&
                    it.name.identifier == originalFunctionName
        } as? SimpleFunctionDescriptor ?: return
        result += createNativeSuspendFunctionDescriptor(thisDescriptor, function)
    }

    private fun createNativeSuspendFunctionDescriptor(
        thisDescriptor: ClassDescriptor,
        suspendFunctionDescriptor: SimpleFunctionDescriptor
    ) = SimpleFunctionDescriptorImpl.create(
        thisDescriptor,
        Annotations.EMPTY,
        Name.identifier("${suspendFunctionDescriptor.name.identifier}$suffix"),
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
            thisDescriptor.builtIns.stringType,
            Modality.FINAL,
            suspendFunctionDescriptor.visibility
        )
    }
}