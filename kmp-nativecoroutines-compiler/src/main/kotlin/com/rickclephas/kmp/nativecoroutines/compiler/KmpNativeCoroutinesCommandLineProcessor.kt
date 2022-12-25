package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KmpNativeCoroutinesCommandLineProcessor: CommandLineProcessor {

    override val pluginId: String = "com.rickclephas.kmp.nativecoroutines"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = EXPOSED_SEVERITY_OPTION_NAME,
            valueDescription = "NONE/WARNING/ERROR",
            description = "Specifies the severity of the exposed coroutines check"
        )
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option.optionName) {
        EXPOSED_SEVERITY_OPTION_NAME -> configuration.put(EXPOSED_SEVERITY_KEY, ExposedSeverity.valueOf(value))
        else -> error("Unexpected config option ${option.optionName}")
    }
}
