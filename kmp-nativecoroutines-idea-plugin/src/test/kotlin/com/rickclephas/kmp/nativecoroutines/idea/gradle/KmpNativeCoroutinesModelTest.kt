package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY
import com.rickclephas.kmp.nativecoroutines.compiler.config.FLOW_REPLAY_CACHE_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.FLOW_VALUE_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.GENERATED_SOURCE_DIR
import com.rickclephas.kmp.nativecoroutines.compiler.config.STATE_FLOW_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.STATE_SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.SUFFIX
import com.rickclephas.kmp.nativecoroutines.compiler.config.SWIFT_EXPORT
import com.rickclephas.kmp.nativecoroutines.gradle.ExposedSeverity as GradleExposedSeverity
import com.rickclephas.kmp.nativecoroutines.gradle.KmpNativeCoroutinesExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class KmpNativeCoroutinesModelTest {

    @Test
    fun `legacy model properties are derived from compiler options`() {
        val options = listOf(
            option(EXPOSED_SEVERITY.optionName, "ERROR"),
            option(GENERATED_SOURCE_DIR.optionName, "/first/generated"),
            option(GENERATED_SOURCE_DIR.optionName, "/second/generated"),
            option(SUFFIX.optionName, "Async"),
            option(FLOW_VALUE_SUFFIX.optionName, "Current"),
            option(FLOW_REPLAY_CACHE_SUFFIX.optionName, "Replay"),
            option(STATE_SUFFIX.optionName, "State"),
            option(STATE_FLOW_SUFFIX.optionName, "Updates"),
            option(SWIFT_EXPORT.optionName, "13"),
        )

        val model: KmpNativeCoroutinesModel = KmpNativeCoroutinesModelImpl(
            compilerPluginOptions = options,
            swiftExportVersion = 13,
        )

        assertEquals(
            options.map { it.key to it.value },
            model.compilerPluginOptions().map { it.key to it.value },
        )
        assertEquals("Async", model.suffix)
        assertEquals("Current", model.flowValueSuffix)
        assertEquals("Replay", model.flowReplayCacheSuffix)
        assertEquals("State", model.stateSuffix)
        assertEquals("Updates", model.stateFlowSuffix)
        assertEquals("ERROR", model.exposedSeverity)
        assertEquals(listOf("/first/generated", "/second/generated"), model.generatedSourceDirs)
        assertEquals(true, model.swiftExport)
        assertEquals(13, model.swiftExportVersion)
    }

    @Test
    fun `omitted nullable suffixes and disabled Swift export remain observable`() {
        val model: KmpNativeCoroutinesModel = KmpNativeCoroutinesModelImpl(
            compilerPluginOptions = listOf(
                option(EXPOSED_SEVERITY.optionName, "WARNING"),
                option(SUFFIX.optionName, "Native"),
                option(STATE_SUFFIX.optionName, "Value"),
                option(SWIFT_EXPORT.optionName, "0"),
            ),
            swiftExportVersion = 13,
        )

        assertNull(model.flowValueSuffix)
        assertNull(model.flowReplayCacheSuffix)
        assertNull(model.stateFlowSuffix)
        assertFalse(model.swiftExport)
        assertEquals(13, model.swiftExportVersion)
    }

    @Test
    fun `missing or invalid Swift export option is treated as disabled`() {
        val missingOption: KmpNativeCoroutinesModel = KmpNativeCoroutinesModelImpl(
            compilerPluginOptions = emptyList(),
            swiftExportVersion = 13,
        )
        val invalidOption: KmpNativeCoroutinesModel = KmpNativeCoroutinesModelImpl(
            compilerPluginOptions = listOf(option(SWIFT_EXPORT.optionName, "invalid")),
            swiftExportVersion = 13,
        )

        assertFalse(missingOption.swiftExport)
        assertFalse(invalidOption.swiftExport)
    }

    @Test
    fun `IDE import reads the canonical Gradle configuration contract`() {
        val extension = object : KmpNativeCoroutinesExtension() {}.apply {
            suffix = "Async"
            flowValueSuffix = null
            flowReplayCacheSuffix = "Replay"
            stateSuffix = "State"
            stateFlowSuffix = null
            exposedSeverity = GradleExposedSeverity.ERROR
            swiftExport = true
        }

        val options = requireNotNull(extension.readCompilerPluginOptions(
            generatedSourceDirs = listOf("/first/generated", "/second/generated"),
        ))

        assertEquals(
            listOf(
                EXPOSED_SEVERITY.optionName to "ERROR",
                GENERATED_SOURCE_DIR.optionName to "/first/generated",
                GENERATED_SOURCE_DIR.optionName to "/second/generated",
                SUFFIX.optionName to "Async",
                FLOW_REPLAY_CACHE_SUFFIX.optionName to "Replay",
                STATE_SUFFIX.optionName to "State",
                SWIFT_EXPORT.optionName to "13",
            ),
            options.map { it.key to it.value },
        )
    }

    @Test
    fun `IDE import falls back to legacy Gradle extension fields`() {
        val extension = object : LegacyGradleExtension() {}

        assertNull(extension.readCompilerPluginOptions(listOf("/generated")))
        assertEquals(
            listOf(
                EXPOSED_SEVERITY.optionName to "ERROR",
                GENERATED_SOURCE_DIR.optionName to "/generated",
                SUFFIX.optionName to "Async",
                FLOW_REPLAY_CACHE_SUFFIX.optionName to "Replay",
                STATE_SUFFIX.optionName to "State",
                SWIFT_EXPORT.optionName to "13",
            ),
            extension.readLegacyCompilerPluginOptions(
                generatedSourceDirs = listOf("/generated"),
                swiftExportVersion = 13,
            ).map { it.key to it.value },
        )
    }

    @Test
    fun `legacy model implementations still produce canonical compiler options`() {
        val model = object : KmpNativeCoroutinesModel {
            override val suffix: String = "Async"
            override val flowValueSuffix: String? = null
            override val flowReplayCacheSuffix: String? = "Replay"
            override val stateSuffix: String = "State"
            override val stateFlowSuffix: String? = null
            override val exposedSeverity: String = "ERROR"
            override val generatedSourceDirs: List<String> = listOf("/generated")
            override val swiftExport: Boolean = true
            override val swiftExportVersion: Long = 13
        }

        assertEquals(
            listOf(
                EXPOSED_SEVERITY.optionName to "ERROR",
                GENERATED_SOURCE_DIR.optionName to "/generated",
                SUFFIX.optionName to "Async",
                FLOW_REPLAY_CACHE_SUFFIX.optionName to "Replay",
                STATE_SUFFIX.optionName to "State",
                SWIFT_EXPORT.optionName to "13",
            ),
            model.compilerPluginOptions().map { it.key to it.value },
        )
    }

    @Test
    fun `canonical compiler options survive tooling model serialization`() {
        val original: KmpNativeCoroutinesModel = KmpNativeCoroutinesModelImpl(
            compilerPluginOptions = listOf(
                option(EXPOSED_SEVERITY.optionName, "WARNING"),
                option(GENERATED_SOURCE_DIR.optionName, "/generated"),
                option(SUFFIX.optionName, "Async"),
                option(STATE_SUFFIX.optionName, "State"),
                option(SWIFT_EXPORT.optionName, "13"),
            ),
            swiftExportVersion = 13,
        )

        val bytes = ByteArrayOutputStream().use { output ->
            ObjectOutputStream(output).use { it.writeObject(original) }
            output.toByteArray()
        }
        val restored = ObjectInputStream(ByteArrayInputStream(bytes)).use {
            it.readObject() as KmpNativeCoroutinesModel
        }

        assertEquals("Async", restored.suffix)
        assertEquals("State", restored.stateSuffix)
        assertEquals(listOf("/generated"), restored.generatedSourceDirs)
        assertEquals(true, restored.swiftExport)
        assertEquals(
            original.compilerPluginOptions().map { it.key to it.value },
            restored.compilerPluginOptions().map { it.key to it.value },
        )
    }

    private fun option(key: String, value: String): KmpNativeCoroutinesCompilerPluginOption =
        KmpNativeCoroutinesCompilerPluginOption(key, value)

    private open class LegacyGradleExtension {
        val suffix: String = "Async"
        val flowValueSuffix: String? = null
        val flowReplayCacheSuffix: String? = "Replay"
        val stateSuffix: String = "State"
        val stateFlowSuffix: String? = null
        val exposedSeverity: GradleExposedSeverity = GradleExposedSeverity.ERROR
        val swiftExport: Boolean = true
    }
}
