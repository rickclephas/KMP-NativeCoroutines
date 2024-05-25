package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableIds
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext

internal class GeneratorContext(
    pluginContext: IrPluginContext
): IrGeneratorContext by pluginContext {

    val asNativeFlowSymbol = pluginContext.referenceFunctions(CallableIds.asNativeFlow).single()
    val nativeSuspendSymbol = pluginContext.referenceFunctions(CallableIds.nativeSuspend).single()

}
