package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.name.FqName

class KmpNativeCoroutinesCommandLineProcessor: CommandLineProcessor {

    override val pluginId: String = "com.rickclephas.kmp.nativecoroutines"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(SUFFIX_OPTION_NAME,
            valueDescription = "string",
            description = "suffix used to generate the native function and property names"),
        CliOption(PROPAGATED_EXCEPTIONS_OPTION_NAME,
            valueDescription = "fqname",
            description = "default Throwable classes that will be propagated as NSError",
            required = false,
            allowMultipleOccurrences = true),
        CliOption(USE_THROWS_ANNOTATION_OPTION_NAME,
            valueDescription = "boolean",
            description = "indicates if the Throws annotation is used to generate the propagatedExceptions list",
            required = false),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option.optionName) {
        SUFFIX_OPTION_NAME -> configuration.put(SUFFIX_KEY, value)
        PROPAGATED_EXCEPTIONS_OPTION_NAME -> configuration.add(PROPAGATED_EXCEPTIONS_KEY, FqName(value))
        USE_THROWS_ANNOTATION_OPTION_NAME -> configuration.put(USE_THROWS_ANNOTATION_KEY, value.toBooleanStrict())
        else -> error("Unexpected config option ${option.optionName}")
    }
}