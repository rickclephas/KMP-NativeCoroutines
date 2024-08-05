package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal enum class FlowType {
    MUTABLE_STATE, STATE,
    SHARED, ABSTRACT, GENERIC
}

internal fun KSClassDeclaration.asFlowType(): FlowType? {
    if (packageName.asString() != "kotlinx.coroutines.flow") return null
    val simpleName = simpleName.asString()
    return when (simpleName) {
        "MutableStateFlow" -> FlowType.MUTABLE_STATE
        "StateFlow" -> FlowType.STATE
        "MutableSharedFlow", "SharedFlow" -> FlowType.SHARED
        "AbstractFlow" -> FlowType.ABSTRACT
        "Flow" -> FlowType.GENERIC
        else -> null
    }
}
