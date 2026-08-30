@file:JvmName("KmpNativeCoroutinesConfigurationContract")

package com.rickclephas.kmp.nativecoroutines.gradle

import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

private const val EXPOSED_SEVERITY_OPTION = "exposedSeverity"
private const val GENERATED_SOURCE_DIR_OPTION = "generatedSourceDir"
private const val SUFFIX_OPTION = "suffix"
private const val FLOW_VALUE_SUFFIX_OPTION = "flowValueSuffix"
private const val FLOW_REPLAY_CACHE_SUFFIX_OPTION = "flowReplayCacheSuffix"
private const val STATE_SUFFIX_OPTION = "stateSuffix"
private const val STATE_FLOW_SUFFIX_OPTION = "stateFlowSuffix"
private const val SWIFT_EXPORT_OPTION = "swiftExport"

/**
 * The single configuration contract consumed by Gradle compilations and IDEA imports.
 *
 * Keep this top-level function and its file JVM name stable: the IDEA tooling model
 * invokes it reflectively so it doesn't need a runtime dependency on the Gradle plugin.
 */
internal fun KmpNativeCoroutinesExtension.compilerPluginOptions(
    generatedSourceDirs: List<String>,
): Map<String, List<String>> = linkedMapOf<String, List<String>>().apply {
    put(EXPOSED_SEVERITY_OPTION, listOf(exposedSeverity.name))
    put(GENERATED_SOURCE_DIR_OPTION, generatedSourceDirs)
    put(SUFFIX_OPTION, listOf(suffix))
    flowValueSuffix?.let { put(FLOW_VALUE_SUFFIX_OPTION, listOf(it)) }
    flowReplayCacheSuffix?.let { put(FLOW_REPLAY_CACHE_SUFFIX_OPTION, listOf(it)) }
    put(STATE_SUFFIX_OPTION, listOf(stateSuffix))
    stateFlowSuffix?.let { put(STATE_FLOW_SUFFIX_OPTION, listOf(it)) }
    val swiftExportMask = swiftExportVersion.takeIf { swiftExport } ?: 0
    put(SWIFT_EXPORT_OPTION, listOf(swiftExportMask.toString()))
}

internal fun KmpNativeCoroutinesExtension.toCompilerPluginOptions(
    generatedSourceDirs: List<String>,
): List<SubpluginOption> = compilerPluginOptions(generatedSourceDirs).flatMap { (key, values) ->
    values.map { value -> SubpluginOption(key, value) }
}
