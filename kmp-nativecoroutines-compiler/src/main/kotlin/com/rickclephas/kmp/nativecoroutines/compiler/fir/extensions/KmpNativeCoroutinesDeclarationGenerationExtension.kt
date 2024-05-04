package com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.*
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.getFunctionSymbols
import com.rickclephas.kmp.nativecoroutines.compiler.fir.utils.getPropertySymbols
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutines
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesIgnore
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesState
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefined
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutinesRefinedState
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation.NativeCoroutineScope
import com.rickclephas.kmp.nativecoroutines.compiler.utils.withSuffix
import com.rickclephas.kmp.nativecoroutines.compiler.utils.withoutSuffix
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

internal class KmpNativeCoroutinesDeclarationGenerationExtension(
    session: FirSession,
    configuration: CompilerConfiguration,
): FirDeclarationGenerationExtension(session) {

    private val predicates = NativeCoroutinesAnnotation.entries.associateWith {
        LookupPredicate.AnnotatedWith(setOf(it.fqName))
    }
    private val lookupPredicate = LookupPredicate.AnnotatedWith(
        NativeCoroutinesAnnotation.entries.map { it.fqName }.toSet()
    )

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(lookupPredicate)
    }

    override fun getCallableNamesForClass(
        classSymbol: FirClassSymbol<*>,
        context: MemberGenerationContext
    ): Set<Name> {
        val names = mutableSetOf<Name>()
        for (symbol in classSymbol.declarationSymbols) {
            if (symbol !is FirCallableSymbol) continue
            names.addAll(getCallableNamesForSymbol(symbol))
        }
        return names
    }

    @OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
    override fun getTopLevelCallableIds(): Set<CallableId> {
        val symbols = session.predicateBasedProvider.getSymbolsByPredicate(lookupPredicate)
        val callableIds = mutableSetOf<CallableId>()
        for (symbol in symbols) {
            if (symbol !is FirCallableSymbol) continue
            val callableId = symbol.callableId
            if (callableId.className != null) continue
            val callableNames = getCallableNamesForSymbol(symbol)
            callableIds.addAll(callableNames.map(callableId::copy))
        }
        return callableIds
    }

    private val suffix = configuration[SUFFIX]
    private val flowValueSuffix = configuration[FLOW_VALUE_SUFFIX]
    private val flowReplayCacheSuffix = configuration[FLOW_REPLAY_CACHE_SUFFIX]
    private val stateSuffix = configuration[STATE_SUFFIX]
    private val stateFlowSuffix = configuration[STATE_FLOW_SUFFIX]

    private fun getCallableNamesForSymbol(symbol: FirCallableSymbol<*>): Set<Name> {
        val annotation = getAnnotationForSymbol(symbol) ?: return emptySet()
        val callableName = symbol.callableId.callableName
        val isProperty = symbol is FirVariableSymbol
        return when (annotation) {
            NativeCoroutines, NativeCoroutinesRefined -> setOfNotNull(
                callableName.withSuffix(suffix),
                callableName.withSuffix(flowValueSuffix?.takeIf { isProperty }),
                callableName.withSuffix(flowReplayCacheSuffix?.takeIf { isProperty })
            )
            NativeCoroutinesState, NativeCoroutinesRefinedState -> when (isProperty) {
                true -> setOfNotNull(
                    callableName.withSuffix(stateSuffix),
                    callableName.withSuffix(stateFlowSuffix)
                )

                false -> emptySet()
            }
            NativeCoroutinesIgnore, NativeCoroutineScope -> emptySet()
        }
    }

    private fun getAnnotationForSymbol(symbol: FirCallableSymbol<*>): NativeCoroutinesAnnotation? =
        predicates.entries.singleOrNull { (_, predicate) -> session.predicateBasedProvider.matches(predicate, symbol) }?.key

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> = buildList {
        generateNativeFunctions(callableId)
    }

    private fun MutableList<FirNamedFunctionSymbol>.generateNativeFunctions(callableId: CallableId) {
        val originalCallableName = callableId.callableName.withoutSuffix(suffix) ?: return
        val symbols = session.symbolProvider.getFunctionSymbols(callableId.copy(originalCallableName))
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            val isRefined = annotation == NativeCoroutinesRefined
            if (!isRefined && annotation != NativeCoroutines) continue
            // TODO: generate native function
        }
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> = buildList {
        generateNativeProperties(callableId)
        generateFlowValueProperties(callableId)
        generateFlowReplayCacheProperties(callableId)
        generateStateProperties(callableId)
        generateStateFlowProperties(callableId)
    }

    private fun MutableList<FirPropertySymbol>.generateNativeProperties(callableId: CallableId) {
        val originalCallableName = callableId.callableName.withoutSuffix(suffix) ?: return
        val symbols = session.symbolProvider.getPropertySymbols(callableId.copy(originalCallableName))
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            val isRefined = annotation == NativeCoroutinesRefined
            if (!isRefined && annotation != NativeCoroutines) continue
            // TODO: generate native property
        }
    }

    private fun MutableList<FirPropertySymbol>.generateFlowValueProperties(callableId: CallableId) {
        val originalCallableName = callableId.callableName.withoutSuffix(flowValueSuffix) ?: return
        val symbols = session.symbolProvider.getPropertySymbols(callableId.copy(originalCallableName))
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            val isRefined = annotation == NativeCoroutinesRefined
            if (!isRefined && annotation != NativeCoroutines) continue
            // TODO: generate flow value property
        }
    }

    private fun MutableList<FirPropertySymbol>.generateFlowReplayCacheProperties(callableId: CallableId) {
        val originalCallableName = callableId.callableName.withoutSuffix(flowReplayCacheSuffix) ?: return
        val symbols = session.symbolProvider.getPropertySymbols(callableId.copy(originalCallableName))
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            val isRefined = annotation == NativeCoroutinesRefined
            if (!isRefined && annotation != NativeCoroutines) continue
            // TODO: generate flow replay cache property
        }
    }

    private fun MutableList<FirPropertySymbol>.generateStateProperties(callableId: CallableId) {
        val originalCallableName = callableId.callableName.withoutSuffix(stateSuffix) ?: return
        val symbols = session.symbolProvider.getPropertySymbols(callableId.copy(originalCallableName))
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            val isRefined = annotation == NativeCoroutinesRefinedState
            if (!isRefined && annotation != NativeCoroutinesState) continue
            // TODO: generate state property
        }
    }

    private fun MutableList<FirPropertySymbol>.generateStateFlowProperties(callableId: CallableId) {
        val originalCallableName = callableId.callableName.withoutSuffix(stateFlowSuffix) ?: return
        val symbols = session.symbolProvider.getPropertySymbols(callableId.copy(originalCallableName))
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            val isRefined = annotation == NativeCoroutinesRefinedState
            if (!isRefined && annotation != NativeCoroutinesState) continue
            // TODO: generate state flow property
        }
    }
}
