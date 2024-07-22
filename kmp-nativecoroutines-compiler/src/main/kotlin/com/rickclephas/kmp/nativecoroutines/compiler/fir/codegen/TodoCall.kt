package com.rickclephas.kmp.nativecoroutines.compiler.fir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableIds
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.ConstantValueKind

internal fun FirSession.buildTodoCall(reason: String): FirFunctionCall = buildFunctionCall {
    val callableId = CallableIds.todo
    val todoSymbol = symbolProvider.getTopLevelFunctionSymbols(callableId.packageName, callableId.callableName)
        .single { it.valueParameterSymbols.size == 1 }
    coneTypeOrNull = StandardClassIds.Nothing.constructClassLikeType()
    @OptIn(SymbolInternals::class)
    val reasonParameter = todoSymbol.valueParameterSymbols.first().fir
    val reasonExpression = buildLiteralExpression(null, ConstantValueKind.String, reason, setType = true)
    argumentList = buildResolvedArgumentList(null, linkedMapOf(reasonExpression to reasonParameter))
    calleeReference = buildResolvedNamedReference {
        name = todoSymbol.name
        resolvedSymbol = todoSymbol
    }
}
