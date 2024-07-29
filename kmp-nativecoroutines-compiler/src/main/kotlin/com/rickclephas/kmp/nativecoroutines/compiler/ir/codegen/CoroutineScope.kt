package com.rickclephas.kmp.nativecoroutines.compiler.ir.codegen

import com.rickclephas.kmp.nativecoroutines.compiler.utils.ClassIds
import com.rickclephas.kmp.nativecoroutines.compiler.utils.NativeCoroutinesAnnotation
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
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
    val viewModelClass = context.getViewModelClass(declaration)
    val property = originalDeclaration.getCoroutineScopeProperty(viewModelClass != null)
        ?: return irCallViewModelScope(declaration, viewModelClass)
            ?: irNull(context.coroutineScopeSymbol.defaultType.makeNullable())
    val getter = property.getter ?: error("CoroutineScope property doesn't have a getter")
    if (getter.extensionReceiverParameter != null) {
        error("CoroutineScope property shouldn't have an extension receiver")
    }
    return irCall(getter).apply {
        if (getter.dispatchReceiverParameter != null) {
            dispatchReceiver = irGet(declaration.dispatchOrExtensionReceiverParameter)
        }
    }
}

@UnsafeDuringIrConstructionAPI
private fun IrSimpleFunction.getCoroutineScopeProperty(classPropertyOnly: Boolean): IrProperty? {
    val parentClass = parentClassOrNull
    val parentClassProperty = parentClass?.getCoroutineScopeProperty()
    if (parentClassProperty != null) return parentClassProperty
    if (classPropertyOnly) return null
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

private val IrSimpleFunction.dispatchOrExtensionReceiverParameter: IrValueParameter
    get() = dispatchReceiverParameter ?: extensionReceiverParameter ?: error("Missing dispatch receiver parameter")

@UnsafeDuringIrConstructionAPI
private fun GeneratorContext.getViewModelClass(declaration: IrSimpleFunction): IrClass? {
    val parentClass = declaration.parentClassOrNull
        ?: declaration.extensionReceiverParameter?.type?.getClass()
        ?: return null
    val viewModelSymbols = listOf(observableViewModelSymbol, androidxViewModelSymbol)
    for (viewModelSymbol in viewModelSymbols) {
        val viewModelClass = viewModelSymbol?.owner ?: continue
        if (parentClass.isSubclassOf(viewModelClass)) return viewModelClass
    }
    return null
}

@UnsafeDuringIrConstructionAPI
private fun IrBuilderWithScope.irCallViewModelScope(
    declaration: IrSimpleFunction,
    viewModelClass: IrClass?,
): IrExpression? = when (viewModelClass?.classId) {
    ClassIds.observableViewModel -> irCallObservableViewModelScope(declaration)
    ClassIds.androidxViewModel -> irCallAndroidXViewModelScope(declaration)
    else -> null
}

@UnsafeDuringIrConstructionAPI
private fun IrBuilderWithScope.irCallObservableViewModelScope(declaration: IrSimpleFunction): IrExpression {
    val context = context as GeneratorContext
    val viewModelScopeGetter = context.observableViewModelScopeSymbol?.owner?.getter
    val coroutineScopeGetter = context.observableCoroutineScopeSymbol?.owner?.getter
    if (viewModelScopeGetter == null || coroutineScopeGetter == null) {
        error("Failed to find viewModelScope.coroutineScope getters")
    }
    return irCall(coroutineScopeGetter).apply {
        extensionReceiver = irCall(viewModelScopeGetter).apply {
            dispatchReceiver = irGet(declaration.dispatchOrExtensionReceiverParameter)
        }
    }
}

@UnsafeDuringIrConstructionAPI
private fun IrBuilderWithScope.irCallAndroidXViewModelScope(declaration: IrSimpleFunction): IrExpression {
    val context = context as GeneratorContext
    val viewModelScopeGetter = context.androidxViewModelScopeSymbol?.owner?.getter
        ?: error("Failed to find viewModelScope getter")
    return irCall(viewModelScopeGetter).apply {
        extensionReceiver = irGet(declaration.dispatchOrExtensionReceiverParameter)
    }
}
