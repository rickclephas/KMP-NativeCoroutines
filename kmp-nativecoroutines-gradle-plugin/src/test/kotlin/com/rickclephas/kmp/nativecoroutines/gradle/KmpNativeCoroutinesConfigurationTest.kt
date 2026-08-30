package com.rickclephas.kmp.nativecoroutines.gradle

import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCommandLineProcessor
import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.ExposedSeverity as CompilerExposedSeverity
import com.rickclephas.kmp.nativecoroutines.compiler.config.FLOW_REPLAY_CACHE_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.FLOW_VALUE_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.GENERATED_SOURCE_DIR
import com.rickclephas.kmp.nativecoroutines.compiler.config.STATE_FLOW_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.STATE_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.SWIFT_EXPORT
import com.rickclephas.kmp.nativecoroutines.compiler.config.SwiftExport
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

@OptIn(CompilerConfiguration.Internals::class)
class KmpNativeCoroutinesConfigurationTest {

    @Test
    fun `default Gradle configuration matches compiler configuration`() {
        val extension = KmpNativeCoroutinesExtension()
        assertEquals(SUFFIX.defaultValue, extension.suffix)
        assertEquals(STATE_SUFFIX.defaultValue, extension.stateSuffix)
        assertEquals(EXPOSED_SEVERITY.defaultValue.name, extension.exposedSeverity.name)
        assertEquals(SWIFT_EXPORT.defaultValue.isNotEmpty(), extension.swiftExport)

        val options = extension.toCompilerPluginOptions(
            generatedSourceDirs = listOf("build/generated"),
        )

        assertEquals(
            KmpNativeCoroutinesCommandLineProcessor().pluginOptions.map { it.optionName }.toSet(),
            options.map { it.key }.toSet(),
        )
        val configuration = options.toCompilerConfiguration()

        assertEquals("Native", configuration[SUFFIX.configKey])
        assertEquals("Value", configuration[FLOW_VALUE_SUFFIX.configKey])
        assertEquals("ReplayCache", configuration[FLOW_REPLAY_CACHE_SUFFIX.configKey])
        assertEquals("Value", configuration[STATE_SUFFIX.configKey])
        assertEquals("Flow", configuration[STATE_FLOW_SUFFIX.configKey])
        assertEquals(CompilerExposedSeverity.WARNING, configuration[EXPOSED_SEVERITY.configKey])
        assertEquals(listOf("build/generated"), configuration.getList(GENERATED_SOURCE_DIR.configKey).map { it.toString() })
        assertEquals(emptySet<SwiftExport>(), configuration[SWIFT_EXPORT.configKey])
    }

    @Suppress("DEPRECATION")
    @Test
    fun `nullable suffixes are omitted and obsolete file suffix is not transported`() {
        val extension = KmpNativeCoroutinesExtension().apply {
            fileSuffix = "Generated"
            flowValueSuffix = null
            flowReplayCacheSuffix = null
            stateFlowSuffix = null
            swiftExport = true
        }

        val options = extension.toCompilerPluginOptions(generatedSourceDirs = emptyList())
        val configuration = options.toCompilerConfiguration()

        assertFalse(options.any { it.key == "fileSuffix" })
        assertNull(configuration[FLOW_VALUE_SUFFIX.configKey])
        assertNull(configuration[FLOW_REPLAY_CACHE_SUFFIX.configKey])
        assertNull(configuration[STATE_FLOW_SUFFIX.configKey])
        assertEquals(
            setOf(
                SwiftExport.NO_FUNC_RETURN_TYPES,
                SwiftExport.SUSPEND_FUNC_SUPPORTED,
                SwiftExport.FLOW_SUPPORTED,
            ),
            configuration[SWIFT_EXPORT.configKey],
        )
    }

    @Test
    fun `custom Gradle configuration reaches compiler unchanged`() {
        val extension = KmpNativeCoroutinesExtension().apply {
            suffix = "Async"
            flowValueSuffix = "Current"
            flowReplayCacheSuffix = "Replay"
            stateSuffix = "State"
            stateFlowSuffix = "Updates"
            exposedSeverity = ExposedSeverity.ERROR
        }

        val configuration = extension.toCompilerPluginOptions(
            generatedSourceDirs = listOf("first/generated", "second/generated"),
        ).toCompilerConfiguration()

        assertEquals("Async", configuration[SUFFIX.configKey])
        assertEquals("Current", configuration[FLOW_VALUE_SUFFIX.configKey])
        assertEquals("Replay", configuration[FLOW_REPLAY_CACHE_SUFFIX.configKey])
        assertEquals("State", configuration[STATE_SUFFIX.configKey])
        assertEquals("Updates", configuration[STATE_FLOW_SUFFIX.configKey])
        assertEquals(CompilerExposedSeverity.ERROR, configuration[EXPOSED_SEVERITY.configKey])
        assertEquals(
            listOf("first/generated", "second/generated"),
            configuration.getList(GENERATED_SOURCE_DIR.configKey).map { it.toString() },
        )
    }

    private fun List<SubpluginOption>.toCompilerConfiguration(): CompilerConfiguration {
        val processor = KmpNativeCoroutinesCommandLineProcessor()
        val optionsByName = processor.pluginOptions.associateBy { it.optionName }
        return CompilerConfiguration().also { configuration ->
            forEach { option ->
                processor.processOption(
                    option = requireNotNull(optionsByName[option.key]),
                    value = option.value,
                    configuration = configuration,
                )
            }
        }
    }
}
