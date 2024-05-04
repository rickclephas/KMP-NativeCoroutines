package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirExtension
import org.jetbrains.kotlin.fir.plugin.*
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.CallableId

/**
 * Creates a top-level or member function depending on whether [owner] is `null`, or not.
 * @see createTopLevelFunction
 * @see createMemberFunction
 */
@ExperimentalTopLevelDeclarationsGenerationApi
internal fun FirExtension.createFunction(
    owner: FirClassSymbol<*>?,
    key: GeneratedDeclarationKey,
    callableId: CallableId,
    returnTypeProvider: (List<FirTypeParameter>) -> ConeKotlinType,
    config: SimpleFunctionBuildingContext.() -> Unit = {}
): FirSimpleFunction = when (owner) {
    null -> createTopLevelFunction(key, callableId, returnTypeProvider, config)
    else -> createMemberFunction(owner, key, callableId.callableName, returnTypeProvider, config)
}

/**
 * Creates a top-level or member property depending on whether [owner] is `null`, or not.
 * @see createTopLevelProperty
 * @see createMemberProperty
 */
@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
internal fun FirExtension.createProperty(
    owner: FirClassSymbol<*>?,
    key: GeneratedDeclarationKey,
    callableId: CallableId,
    returnTypeProvider: (List<FirTypeParameterRef>) -> ConeKotlinType,
    isVal: Boolean = true,
    hasBackingField: Boolean = true,
    config: PropertyBuildingContext.() -> Unit = {}
): FirProperty = when (owner) {
    null -> createTopLevelProperty(key, callableId, returnTypeProvider, isVal, hasBackingField, config)
    else -> createMemberProperty(owner, key, callableId.callableName, returnTypeProvider, isVal, hasBackingField, config)
}
