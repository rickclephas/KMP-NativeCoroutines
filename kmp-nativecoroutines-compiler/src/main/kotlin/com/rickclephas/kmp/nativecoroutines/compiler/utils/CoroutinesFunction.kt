package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.effectiveVisibility

private val SimpleFunctionDescriptor.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && (isSuspend || hasFlowReturnType)

internal val SimpleFunctionDescriptor.needsNativeFunction: Boolean
    get() = effectiveVisibility().publicApi &&
            !hasIgnoreAnnotation &&
            overriddenDescriptors.size != 1 &&
            isCoroutinesFunction
