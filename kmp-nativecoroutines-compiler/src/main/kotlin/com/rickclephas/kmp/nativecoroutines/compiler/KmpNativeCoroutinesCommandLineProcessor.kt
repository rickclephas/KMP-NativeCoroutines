package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.config.*
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
public class KmpNativeCoroutinesCommandLineProcessor: CommandLineProcessor {

    override val pluginId: String = "com.rickclephas.kmp.nativecoroutines"
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        EXPOSED_SEVERITY, GENERATED_SOURCE_DIR, SWIFT_EXPORT,
        SUFFIX, FLOW_VALUE_SUFFIX, FLOW_REPLAY_CACHE_SUFFIX, STATE_SUFFIX, STATE_FLOW_SUFFIX
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ): Unit = when (option) {
        is ConfigOption<*> -> configuration[option] = value
        is ConfigListOption<*> -> configuration.add(option, value)
        else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
}
