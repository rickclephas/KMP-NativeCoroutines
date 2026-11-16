package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.externalSystem.model.Key
import com.rickclephas.kmp.nativecoroutines.compiler.config.*
import java.io.Serializable

public interface KmpNativeCoroutinesModel: Serializable {
    public val suffix: String
    public val flowValueSuffix: String?
    public val flowReplayCacheSuffix: String?
    public val stateSuffix: String
    public val stateFlowSuffix: String?
    public val exposedSeverity: String
    public val generatedSourceDirs: List<String>
    public val swiftExport: Boolean
    public val swiftExportVersion: Long
}

internal class KmpNativeCoroutinesCompilerPluginOption(
    val key: String,
    val value: String,
): Serializable

internal class KmpNativeCoroutinesModelImpl(
    val compilerPluginOptions: List<KmpNativeCoroutinesCompilerPluginOption>,
    override val swiftExportVersion: Long,
): KmpNativeCoroutinesModel {

    override val suffix: String
        get() = compilerPluginOptions.firstValue(SUFFIX) ?: SUFFIX.defaultValue
    override val flowValueSuffix: String?
        get() = compilerPluginOptions.firstValue(FLOW_VALUE_SUFFIX)
    override val flowReplayCacheSuffix: String?
        get() = compilerPluginOptions.firstValue(FLOW_REPLAY_CACHE_SUFFIX)
    override val stateSuffix: String
        get() = compilerPluginOptions.firstValue(STATE_SUFFIX) ?: STATE_SUFFIX.defaultValue
    override val stateFlowSuffix: String?
        get() = compilerPluginOptions.firstValue(STATE_FLOW_SUFFIX)
    override val exposedSeverity: String
        get() = compilerPluginOptions.firstValue(EXPOSED_SEVERITY) ?: EXPOSED_SEVERITY.defaultValue.name
    override val generatedSourceDirs: List<String>
        get() = compilerPluginOptions.allValues(GENERATED_SOURCE_DIR)
    override val swiftExport: Boolean
        get() = compilerPluginOptions.firstValue(SWIFT_EXPORT)
            ?.toLongOrNull()
            ?.let { it != 0L }
            ?: false
}

private fun List<KmpNativeCoroutinesCompilerPluginOption>.firstValue(option: ConfigOption<*>): String? =
    firstOrNull { it.key == option.optionName }?.value

private fun List<KmpNativeCoroutinesCompilerPluginOption>.allValues(option: ConfigListOption<*>): List<String> =
    filter { it.key == option.optionName }.map { it.value }

internal fun KmpNativeCoroutinesModel.compilerPluginOptions(): List<KmpNativeCoroutinesCompilerPluginOption> {
    if (this is KmpNativeCoroutinesModelImpl) return compilerPluginOptions
    // Compatibility adapter for models produced by older IDEA/Gradle plugin pairs.
    return buildList {
        add(KmpNativeCoroutinesCompilerPluginOption(EXPOSED_SEVERITY.optionName, exposedSeverity))
        generatedSourceDirs.forEach {
            add(KmpNativeCoroutinesCompilerPluginOption(GENERATED_SOURCE_DIR.optionName, it))
        }
        add(KmpNativeCoroutinesCompilerPluginOption(SUFFIX.optionName, suffix))
        flowValueSuffix?.let {
            add(KmpNativeCoroutinesCompilerPluginOption(FLOW_VALUE_SUFFIX.optionName, it))
        }
        flowReplayCacheSuffix?.let {
            add(KmpNativeCoroutinesCompilerPluginOption(FLOW_REPLAY_CACHE_SUFFIX.optionName, it))
        }
        add(KmpNativeCoroutinesCompilerPluginOption(STATE_SUFFIX.optionName, stateSuffix))
        stateFlowSuffix?.let {
            add(KmpNativeCoroutinesCompilerPluginOption(STATE_FLOW_SUFFIX.optionName, it))
        }
        val swiftExportMask = swiftExportVersion.takeIf { swiftExport } ?: 0
        add(KmpNativeCoroutinesCompilerPluginOption(SWIFT_EXPORT.optionName, swiftExportMask.toString()))
    }
}

internal val KmpNativeCoroutinesModelKey = Key<KmpNativeCoroutinesModel>(KmpNativeCoroutinesModel::class.java.name, 1)
