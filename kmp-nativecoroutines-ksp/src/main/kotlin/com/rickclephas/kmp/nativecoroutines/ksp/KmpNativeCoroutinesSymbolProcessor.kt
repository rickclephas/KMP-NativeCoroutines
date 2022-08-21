package com.rickclephas.kmp.nativecoroutines.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

internal class KmpNativeCoroutinesSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: KmpNativeCoroutinesOptions
): SymbolProcessor {

    private val coroutineScopeProvider = CoroutineScopeProvider(logger)

    private val fileSpecBuilders = mutableMapOf<String, FileSpec.Builder>()

    private fun KSFile.getFileSpecBuilder(): FileSpec.Builder = fileSpecBuilders.getOrPut(filePath) {
        FileSpec.builder(packageName.asString(), "${fileName.removeSuffix(".kt")}${options.fileSuffix}")
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        coroutineScopeProvider.process(resolver)
        val deferredSymbols = mutableListOf<KSAnnotated>()
        resolver.getSymbolsWithAnnotation(nativeCoroutinesAnnotationName).forEach { symbol ->
            when (symbol) {
                is KSPropertyDeclaration -> symbol.takeUnless(::process)?.let(deferredSymbols::add)
                is KSFunctionDeclaration -> symbol.takeUnless(::process)?.let(deferredSymbols::add)
                else -> logger.warn("Unsupported symbol type", symbol)
            }
        }
        if (deferredSymbols.isEmpty()) {
            fileSpecBuilders.forEach { (_, fileSpecBuilder) ->
                fileSpecBuilder.build().writeTo(codeGenerator, true)
            }
            fileSpecBuilders.clear()
        }
        return deferredSymbols
    }

    private fun process(property: KSPropertyDeclaration): Boolean {
        if (!property.validate()) return false
        val file = property.containingFile
        if (file == null) {
            logger.error("Property isn't contained in a source file", property)
            return true
        }
        val scopeProperty = coroutineScopeProvider.getScopeProperty(property) ?: return false
        val propertySpecs = property.toNativeCoroutinesPropertySpecs(scopeProperty, options.suffix) ?: return false
        val fileSpecBuilder = file.getFileSpecBuilder()
        propertySpecs.forEach(fileSpecBuilder::addProperty)
        return true
    }

    private fun process(function: KSFunctionDeclaration): Boolean {
        if (!function.validate()) return false
        val file = function.containingFile
        if (file == null) {
            logger.error("Function isn't contained in a source file", function)
            return true
        }
        val scopeProperty = coroutineScopeProvider.getScopeProperty(function) ?: return false
        val funSpec = function.toNativeCoroutinesFunSpec(scopeProperty, options.suffix) ?: return false
        file.getFileSpecBuilder().addFunction(funSpec)
        return true
    }
}
