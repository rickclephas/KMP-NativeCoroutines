package com.rickclephas.kmp.nativecoroutines.compiler.utils

import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

internal val SimpleFunctionDescriptor.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && isSuspend // TODO: Support functions that return a Flow

internal val IrSimpleFunction.isCoroutinesFunction: Boolean
    get() = !name.isSpecial && isSuspend // TODO: Support functions that return a Flow

internal val IrFunction.isNativeCoroutinesFunction: Boolean
    get() = !name.isSpecial && returnType.isNativeSuspend // TODO: Support functions that return a Flow