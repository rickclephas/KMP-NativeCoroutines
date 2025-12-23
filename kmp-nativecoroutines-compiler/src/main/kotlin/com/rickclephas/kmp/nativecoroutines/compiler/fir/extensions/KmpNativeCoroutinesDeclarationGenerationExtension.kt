package com.rickclephas.kmp.nativecoroutines.compiler.fir.extensions

import com.rickclephas.kmp.nativecoroutines.compiler.config.*
import com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen.buildNativeFunction
import com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen.buildNativeProperty
import com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen.buildSharedFlowReplayCacheProperty
import com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen.buildStateFlowValueProperty
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
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.utils.isExpect
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.scopes.getFunctions
import org.jetbrains.kotlin.fir.scopes.getProperties
import org.jetbrains.kotlin.fir.scopes.impl.declaredMemberScope
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addIfNotNull

@OptIn(DirectDeclarationsAccess::class)
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
            val callableId = symbol.callableId ?: continue
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
    private val swiftExport = configuration[SWIFT_EXPORT]

    private fun getCallableNamesForSymbol(symbol: FirCallableSymbol<*>): Set<Name> {
        val annotation = getAnnotationForSymbol(symbol) ?: return emptySet()
        val callableName = symbol.callableId?.callableName ?: return emptySet()
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

    private fun getAnnotationForSymbol(symbol: FirCallableSymbol<*>): NativeCoroutinesAnnotation? {
        if (symbol.rawStatus.isOverride || symbol.isExpect) return null
        return predicates.entries.singleOrNull { (_, predicate) ->
            session.predicateBasedProvider.matches(predicate, symbol)
        }?.key
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> = buildList {
        generateFunctions(
            context?.owner,
            callableId, suffix,
            setOf(NativeCoroutines, NativeCoroutinesRefined)
        ) { symbol, annotation ->
            buildNativeFunction(callableId, symbol, annotation, swiftExport)
        }
    }

    private fun MutableList<FirNamedFunctionSymbol>.generateFunctions(
        owner: FirClassSymbol<*>?,
        callableId: CallableId,
        suffix: String?,
        annotations: Set<NativeCoroutinesAnnotation>,
        generateFunction: (FirNamedFunctionSymbol, NativeCoroutinesAnnotation) -> FirNamedFunctionSymbol?
    ) {
        val originalCallableName = callableId.callableName.withoutSuffix(suffix) ?: return
        val symbols = when (owner) {
            null -> session.symbolProvider.getTopLevelFunctionSymbols(callableId.packageName, originalCallableName)
            else -> owner.declaredMemberScope(session, null).getFunctions(originalCallableName)
        }
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            if (annotation !in annotations) continue
            addIfNotNull(generateFunction(symbol, annotation))
        }
    }

    override fun generateProperties(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirPropertySymbol> = buildList {
        generateProperties(
            context?.owner,
            callableId, suffix,
            setOf(NativeCoroutines, NativeCoroutinesRefined)
        ) { symbol, annotation ->
            buildNativeProperty(callableId, symbol, annotation, objCName = symbol.name.identifier, swiftExport = swiftExport)
        }
        generateProperties(
            context?.owner,
            callableId, flowValueSuffix,
            setOf(NativeCoroutines, NativeCoroutinesRefined)
        ) { symbol, annotation ->
            buildStateFlowValueProperty(callableId, symbol, annotation, objCNameSuffix = flowValueSuffix, swiftExport = swiftExport)
        }
        generateProperties(
            context?.owner,
            callableId, flowReplayCacheSuffix,
            setOf(NativeCoroutines, NativeCoroutinesRefined)
        ) { symbol, annotation ->
            buildSharedFlowReplayCacheProperty(callableId, symbol, annotation, flowReplayCacheSuffix, swiftExport)
        }
        generateProperties(
            context?.owner,
            callableId, stateSuffix,
            setOf(NativeCoroutinesState, NativeCoroutinesRefinedState)
        ) { symbol, annotation ->
            buildStateFlowValueProperty(callableId, symbol, annotation, objCName = symbol.name.identifier, swiftExport = swiftExport)
        }
        generateProperties(
            context?.owner,
            callableId, stateFlowSuffix,
            setOf(NativeCoroutinesState, NativeCoroutinesRefinedState)
        ) { symbol, annotation ->
            buildNativeProperty(callableId, symbol, annotation, objCNameSuffix = stateFlowSuffix, swiftExport = swiftExport)
        }
    }

    private fun MutableList<FirPropertySymbol>.generateProperties(
        owner: FirClassSymbol<*>?,
        callableId: CallableId,
        suffix: String?,
        annotations: Set<NativeCoroutinesAnnotation>,
        generateProperty: (FirPropertySymbol, NativeCoroutinesAnnotation) -> FirPropertySymbol?
    ) {
        val originalCallableName = callableId.callableName.withoutSuffix(suffix) ?: return
        val symbols = when (owner) {
            null -> session.symbolProvider.getTopLevelPropertySymbols(callableId.packageName, originalCallableName)
            else -> owner.declaredMemberScope(session, null)
                .getProperties(originalCallableName).filterIsInstance<FirPropertySymbol>()
        }
        for (symbol in symbols) {
            val annotation = getAnnotationForSymbol(symbol) ?: continue
            if (annotation !in annotations) continue
            if (symbol.receiverParameterSymbol != null && symbol.dispatchReceiverType != null) continue
            addIfNotNull(generateProperty(symbol, annotation))
        }
    }
}
