package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI

@UnsafeDuringIrConstructionAPI
internal class GeneratorContext(
    pluginContext: IrPluginContext
): IrGeneratorContext by pluginContext {

    val coroutineScopeSymbol = pluginContext.referenceClass(ClassIds.coroutineScope)!!

    private val asNativeFlowSymbols = pluginContext.referenceFunctions(CallableIds.asNativeFlow)
    val asNativeFlowSymbol = asNativeFlowSymbols.single { it.owner.typeParameters.isNotEmpty() }
    val asNativeFlowUnitSymbol = asNativeFlowSymbols.single { it.owner.typeParameters.isEmpty() }

    private val nativeSuspendSymbols = pluginContext.referenceFunctions(CallableIds.nativeSuspend)
    val nativeSuspendSymbol = nativeSuspendSymbols.single { it.owner.typeParameters.isNotEmpty() }
    val nativeSuspendUnitSymbol = nativeSuspendSymbols.single { it.owner.typeParameters.isEmpty() }

    val sharedFlowReplayCacheSymbol = pluginContext.referenceProperties(CallableIds.sharedFlowReplayCache).single()
    val stateFlowValueSymbol = pluginContext.referenceProperties(CallableIds.stateFlowValue).single()
    val mutableStateFlowValueSymbol = pluginContext.referenceProperties(CallableIds.mutableStateFlowValue).single()

    val observableViewModelSymbol = pluginContext.referenceClass(ClassIds.observableViewModel)
    val observableViewModelScopeSymbol = pluginContext.referenceProperties(CallableIds.observableViewModelScope).singleOrNull()
    val observableCoroutineScopeSymbol = pluginContext.referenceProperties(CallableIds.observableCoroutineScope).singleOrNull()
    val androidxViewModelSymbol = pluginContext.referenceClass(ClassIds.androidxViewModel)
    val androidxViewModelScopeSymbol = pluginContext.referenceProperties(CallableIds.androidxViewModelScope).singleOrNull()
}
