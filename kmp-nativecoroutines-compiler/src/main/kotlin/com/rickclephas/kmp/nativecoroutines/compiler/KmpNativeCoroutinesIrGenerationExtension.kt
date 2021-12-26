package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.utils.NameGenerator
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.name.FqName

internal class KmpNativeCoroutinesIrGenerationExtension(
    private val nameGenerator: NameGenerator,
    private val propagatedExceptions: List<FqName>,
    private val useThrowsAnnotation: Boolean
): IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        KmpNativeCoroutinesIrTransformer(pluginContext, nameGenerator, propagatedExceptions, useThrowsAnnotation)
            .let { moduleFragment.accept(it, null) }
    }
}

