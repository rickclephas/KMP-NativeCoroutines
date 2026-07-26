package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.ir.utils.IrBlockBodyExpression
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.passTypeArgumentsFrom
import org.jetbrains.kotlin.ir.util.substitute

internal fun irCallOriginalPropertyGetter(
    originalGetter: IrSimpleFunction,
    propertyFunction: IrSimpleFunction
): IrBlockBodyExpression {
    val typeArgs = propertyFunction.typeParameters.map { it.defaultType }
    val returnType = originalGetter.returnType.substitute(originalGetter.typeParameters, typeArgs)
    return IrBlockBodyExpression(returnType) {
        irCall(originalGetter.symbol, returnType).apply {
            propertyFunction.parameters.filter { it.kind != IrParameterKind.Regular }.forEachIndexed { index, parameter ->
                arguments[index] = irGet(parameter)
            }
            passTypeArgumentsFrom(propertyFunction)
        }
    }
}
