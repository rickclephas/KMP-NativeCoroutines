package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext

internal class GeneratorContext(
    pluginContext: IrPluginContext
): IrGeneratorContext by pluginContext {

    val coroutineScopeSymbol = pluginContext.referenceClass(ClassIds.coroutineScope)!!

    val asNativeFlowSymbol = pluginContext.referenceFunctions(CallableIds.asNativeFlow).single()
    val nativeSuspendSymbol = pluginContext.referenceFunctions(CallableIds.nativeSuspend).single()

    val sharedFlowReplayCacheSymbol = pluginContext.referenceProperties(CallableIds.sharedFlowReplayCache).single()
    val stateFlowValueSymbol = pluginContext.referenceProperties(CallableIds.stateFlowValue).single()
    val mutableStateFlowValueSymbol = pluginContext.referenceProperties(CallableIds.mutableStateFlowValue).single()
}
