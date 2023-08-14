package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ksp.toClassName

internal class CoroutineScopeProvider(
    private val logger: KSPLogger
) {

    private companion object {
        val KSClassDeclaration.scopePropertyKey: String
            get() = "class://${toClassName().canonicalName}"

        val KSFile.scopePropertyKey: String
            get() = "file://${filePath}"
    }

    data class ScopeProperty(
        val code: String,
        val codeArg: List<Any>,
        val containingFile: KSFile?
    ) {
        companion object {
            val DEFAULT = ScopeProperty("null", emptyList(), null)

            fun viewModelScope(containingFile: KSFile): ScopeProperty = ScopeProperty("%N.%M", listOf(
                "viewModelScope",
                MemberName("com.rickclephas.kmm.viewmodel", "coroutineScope", true)
            ), containingFile)
        }
    }

    private val scopeProperties = mutableMapOf<String, ScopeProperty>()

    fun process(resolver: Resolver) {
        resolver.getSymbolsWithAnnotation(nativeCoroutineScopeAnnotationName).forEach { symbol ->
            if (symbol !is KSPropertyDeclaration) {
                logger.warn("Unsupported symbol type", symbol)
                return@forEach
            }
            process(symbol)
        }
    }

    private fun process(property: KSPropertyDeclaration) {
        val classDeclaration = property.parentDeclaration as? KSClassDeclaration
        val file = property.containingFile ?: run {
            logger.error("Property isn't contained in a source file", property)
            return
        }
        val code: String
        val codeArg: Any
        if (classDeclaration == null) {
            val isExtension = property.extensionReceiver != null
            codeArg = MemberName(property.packageName.asString(), property.simpleName.asString(), isExtension)
            code = "%M"
        } else {
            codeArg = property.simpleName.asString()
            code = "%N"
        }
        val scopeProperty = ScopeProperty(code, listOf(codeArg), file)
        if (classDeclaration == null) {
            if (scopeProperties.putIfAbsent(file.scopePropertyKey, scopeProperty) != null) {
                logger.warn("Ignoring duplicate scope property", property)
            }
        } else {
            if (scopeProperties.putIfAbsent(classDeclaration.scopePropertyKey, scopeProperty) != null) {
                logger.warn("Ignoring duplicate scope property", property)
            }
        }
    }

    fun getScopeProperty(declaration: KSDeclaration): ScopeProperty? {
        val classDeclaration = declaration.parentDeclaration as? KSClassDeclaration
        if (classDeclaration != null) {
            val classScopeProperty = classDeclaration.let(::getScopeProperty) ?: return null
            if (classScopeProperty != ScopeProperty.DEFAULT) return classScopeProperty
        }
        val file = declaration.containingFile ?: run {
            logger.error("Declaration isn't contained in a source file", declaration)
            return null
        }
        scopeProperties[file.scopePropertyKey]?.let { return it }
        if (classDeclaration == null) {
            val receiverClassDeclaration = when (declaration) {
                is KSPropertyDeclaration -> declaration.extensionReceiver
                is KSFunctionDeclaration -> declaration.extensionReceiver
                else -> {
                    logger.warn("Unsupported declaration type", declaration)
                    null
                }
            }?.resolve()?.declaration as? KSClassDeclaration
            if (receiverClassDeclaration != null) {
                val receiverScopeProperty = receiverClassDeclaration.let(::getScopeProperty) ?: return null
                if (receiverScopeProperty != ScopeProperty.DEFAULT) return receiverScopeProperty
            }
        }
        return ScopeProperty.DEFAULT
    }

    private fun getScopeProperty(classDeclaration: KSClassDeclaration): ScopeProperty? {
        var containingFile: KSFile = classDeclaration.containingFile ?: return ScopeProperty.DEFAULT
        scopeProperties[classDeclaration.scopePropertyKey]?.let { return it }
        classDeclaration.getAllSuperTypes().forEach { superType ->
            if (superType.isError) return null
            val superClassDeclaration = superType.declaration as? KSClassDeclaration ?: return@forEach
            scopeProperties[superClassDeclaration.scopePropertyKey]?.let { return it }
            // If this class is a KMMViewModel, use the ViewModelScope
            if (superClassDeclaration.isKMMViewModel()) return ScopeProperty.viewModelScope(containingFile)
            containingFile = superClassDeclaration.containingFile ?: containingFile
        }
        return ScopeProperty.DEFAULT
    }

    private fun KSClassDeclaration.isKMMViewModel(): Boolean =
        packageName.asString() == "com.rickclephas.kmm.viewmodel" && simpleName.asString() == "KMMViewModel"
}
