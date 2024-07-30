package com.rickclephas.kmp.nativecoroutines.compiler.ir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.FqNames
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.getAllSubstitutedSupertypes
import org.jetbrains.kotlin.ir.util.hasEqualFqName
import org.jetbrains.kotlin.ir.util.substitute

private val flowFqNames = listOf(FqNames.flow, FqNames.stateFlow)

@UnsafeDuringIrConstructionAPI
internal fun IrType.getFlowValueTypeArg(): IrTypeArgument {
    val irClass = classOrFail.owner
    this as IrSimpleType // smartcast not provided by classOrFail
    if (flowFqNames.any(irClass::hasEqualFqName)) {
        return arguments.first()
    }
    var flowType = getAllSubstitutedSupertypes(irClass).firstOrNull {
        flowFqNames.any(it.classOrFail.owner::hasEqualFqName)
    } ?: error("$this is not a Flow type")
    val arguments = arguments.map { it.typeOrFail }
    flowType = flowType.substitute(irClass.typeParameters, arguments) as IrSimpleType
    return flowType.arguments.first()
}
