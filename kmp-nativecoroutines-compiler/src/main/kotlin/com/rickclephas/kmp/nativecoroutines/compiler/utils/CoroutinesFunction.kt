package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.effectiveVisibility
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

internal val SimpleFunctionDescriptor.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && (isSuspend || hasFlowReturnType)

internal val SimpleFunctionDescriptor.needsNativeFunction: Boolean
    get() = effectiveVisibility().publicApi &&
            !hasIgnoreAnnotation &&
            overriddenDescriptors.isEmpty() &&
            isCoroutinesFunction

internal val IrSimpleFunction.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && (isSuspend || returnType.isFlowType)

internal val IrSimpleFunction.needsNativeFunction: Boolean
    get() = visibility.isPublicAPI &&
            !hasIgnoreAnnotation &&
            overriddenSymbols.isEmpty() &&
            isCoroutinesFunction

internal val IrFunction.isNativeCoroutinesFunction: Boolean
    get() = !name.isSpecial && (returnType.isNativeSuspend || returnType.isNativeFlow)