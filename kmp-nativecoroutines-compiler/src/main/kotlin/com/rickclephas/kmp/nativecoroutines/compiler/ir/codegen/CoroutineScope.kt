package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.fileOrNull
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallCoroutineScope(declaration: IrSimpleFunction): IrExpression {
    val property = declaration.parentClassOrNull?.getCoroutineScopeProperty()
        ?: declaration.fileOrNull?.getCoroutineScopeProperty()
        ?: return irNull()
    val getter = property.getter ?: error("CoroutineScope property doesn't have a getter")
    if (getter.extensionReceiverParameter != null) {
        error("CoroutineScope property shouldn't have an extension receiver")
    }
    return irCall(getter).apply {
        if (getter.dispatchReceiverParameter != null) {
            val dispatchReceiverParameter = declaration.dispatchReceiverParameter
                ?: error("Missing dispatch receiver parameter")
            dispatchReceiver = irGet(dispatchReceiverParameter)
        }
    }
}

@UnsafeDuringIrConstructionAPI
private fun IrDeclarationContainer.getCoroutineScopeProperty(): IrProperty? =
    declarations.filterIsInstance<IrProperty>().firstOrNull {
        it.annotations.hasAnnotation(NativeCoroutinesAnnotation.NativeCoroutineScope.fqName)
    }
