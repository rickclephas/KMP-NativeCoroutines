package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.*

@UnsafeDuringIrConstructionAPI
internal fun IrBuilderWithScope.irCallCoroutineScope(
    originalDeclaration: IrSimpleFunction,
    declaration: IrSimpleFunction
): IrExpression {
    val context = context as GeneratorContext
    val property = originalDeclaration.getCoroutineScopeProperty()
        ?: return irNull(context.coroutineScopeSymbol.defaultType.makeNullable())
    val getter = property.getter ?: error("CoroutineScope property doesn't have a getter")
    if (getter.extensionReceiverParameter != null) {
        error("CoroutineScope property shouldn't have an extension receiver")
    }
    return irCall(getter).apply {
        if (getter.dispatchReceiverParameter != null) {
            val dispatchReceiverParameter = declaration.dispatchReceiverParameter
                ?: declaration.extensionReceiverParameter
                ?: error("Missing dispatch receiver parameter")
            dispatchReceiver = irGet(dispatchReceiverParameter)
        }
    }
}

@UnsafeDuringIrConstructionAPI
private fun IrSimpleFunction.getCoroutineScopeProperty(): IrProperty? {
    val parentClass = parentClassOrNull
    val parentClassProperty = parentClass?.getCoroutineScopeProperty()
    if (parentClassProperty != null) return parentClassProperty
    // TODO: support KMP-ObservableViewModel viewModelScope
    fileOrNull?.getCoroutineScopeProperty()?.let { return it }
    if (parentClass != null) return null
    val extensionClassProperty = extensionReceiverParameter?.type?.getClass()?.getCoroutineScopeProperty()
    if (extensionClassProperty != null) return extensionClassProperty
    return null
}

@UnsafeDuringIrConstructionAPI
private fun IrDeclarationContainer.getCoroutineScopeProperty(): IrProperty? =
    declarations.filterIsInstance<IrProperty>().firstOrNull {
        it.annotations.hasAnnotation(NativeCoroutinesAnnotation.NativeCoroutineScope.fqName)
    }
