package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.config.ConfigOption
import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.set
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
public class KmpNativeCoroutinesCommandLineProcessor: CommandLineProcessor {

    override val pluginId: String = "com.rickclephas.kmp.nativecoroutines"
    override val pluginOptions: Collection<AbstractCliOption> = listOf(EXPOSED_SEVERITY)

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ): Unit = when (option) {
        is ConfigOption<*> -> configuration[option] = value
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}
