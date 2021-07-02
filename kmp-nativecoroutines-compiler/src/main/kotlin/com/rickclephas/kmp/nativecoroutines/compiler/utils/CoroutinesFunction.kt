package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

internal val SimpleFunctionDescriptor.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && (isSuspend || hasFlowReturnType)

internal val IrSimpleFunction.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && (isSuspend || returnType.isFlowType)

internal val IrFunction.isNativeCoroutinesFunction: Boolean
    get() = !name.isSpecial && (returnType.isNativeSuspend || returnType.isNativeFlow)