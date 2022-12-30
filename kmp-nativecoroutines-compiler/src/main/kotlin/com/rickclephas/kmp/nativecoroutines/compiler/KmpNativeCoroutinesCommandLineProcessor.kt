package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesConfigurationKeys as ConfigurationKeys
import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesOptionNames as OptionNames
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KmpNativeCoroutinesCommandLineProcessor: CommandLineProcessor {

    companion object {
        val EXPOSED_SEVERITY = CliOption(
            optionName = OptionNames.EXPOSED_SEVERITY,
            valueDescription = "NONE/WARNING/ERROR",
            description = "Specifies the severity of the exposed coroutines check"
        )
    }

    override val pluginId: String = "com.rickclephas.kmp.nativecoroutines"
    override val pluginOptions: Collection<AbstractCliOption> = listOf(EXPOSED_SEVERITY)

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option) {
        EXPOSED_SEVERITY -> configuration.put(ConfigurationKeys.EXPOSED_SEVERITY, ExposedSeverity.valueOf(value))
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}
