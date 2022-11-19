package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.effectiveVisibility

private val PropertyDescriptor.isCoroutinesProperty: Boolean
    get() = !name.isSpecial && hasFlowType

internal val PropertyDescriptor.needsNativeProperty: Boolean
    get() = effectiveVisibility().publicApi &&
            !hasIgnoreAnnotation &&
            overriddenDescriptors.size != 1 &&
            isCoroutinesProperty
