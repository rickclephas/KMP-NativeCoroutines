package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext

internal class GeneratorContext(
    pluginContext: IrPluginContext
): IrGeneratorContext by pluginContext {

    val coroutineScopeSymbol = pluginContext.finderForBuiltins().findClass(ClassIds.coroutineScope)!!

    val asNativeFlowSymbol = pluginContext.finderForBuiltins().findFunctions(CallableIds.asNativeFlow).single()
    val nativeSuspendSymbol = pluginContext.finderForBuiltins().findFunctions(CallableIds.nativeSuspend).single()

    val sharedFlowReplayCacheSymbol = pluginContext.finderForBuiltins().findProperties(CallableIds.sharedFlowReplayCache).single()
    val stateFlowValueSymbol = pluginContext.finderForBuiltins().findProperties(CallableIds.stateFlowValue).single()
    val mutableStateFlowValueSymbol = pluginContext.finderForBuiltins().findProperties(CallableIds.mutableStateFlowValue).single()

    val observableViewModelSymbol = pluginContext.finderForBuiltins().findClass(ClassIds.observableViewModel)
    val observableViewModelScopeSymbol = pluginContext.finderForBuiltins().findProperties(CallableIds.observableViewModelScope).singleOrNull()
    val observableCoroutineScopeSymbol = pluginContext.finderForBuiltins().findProperties(CallableIds.observableCoroutineScope).singleOrNull()
    val androidxViewModelSymbol = pluginContext.finderForBuiltins().findClass(ClassIds.androidxViewModel)
    val androidxViewModelScopeSymbol = pluginContext.finderForBuiltins().findProperties(CallableIds.androidxViewModelScope).singleOrNull()
}
