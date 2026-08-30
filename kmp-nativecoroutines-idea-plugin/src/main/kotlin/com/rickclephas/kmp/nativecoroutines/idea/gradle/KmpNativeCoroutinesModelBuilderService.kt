package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.rickclephas.kmp.nativecoroutines.compiler.config.*
import org.gradle.api.Project
import org.jetbrains.plugins.gradle.tooling.AbstractModelBuilderService
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext
import java.lang.Exception

private const val CONFIGURATION_CONTRACT_CLASS =
    "com.rickclephas.kmp.nativecoroutines.gradle.KmpNativeCoroutinesConfigurationContract"
private const val CONFIGURATION_CONTRACT_METHOD = "compilerPluginOptions"

public class KmpNativeCoroutinesModelBuilderService: AbstractModelBuilderService() {

    override fun canBuild(modelName: String?): Boolean = modelName == KmpNativeCoroutinesModel::class.java.name

    override fun buildAll(modelName: String, project: Project, context: ModelBuilderContext): KmpNativeCoroutinesModel? {
        if (project.plugins.findPlugin("com.rickclephas.kmp.nativecoroutines") == null) return null
        val extension = project.extensions.findByName("nativeCoroutines") ?: return null

        val generatedSourceDirs = extension.readField<List<Any>>("generatedSourceDirs").orEmpty()
            .map { project.file(it).absolutePath }.distinct()
        val swiftExportVersion = extension.readField<Long>("swiftExportVersion") ?: 0
        val compilerPluginOptions = extension.readCompilerPluginOptions(generatedSourceDirs)
            // Released Gradle plugin versions predate the shared contract.
            ?: extension.readLegacyCompilerPluginOptions(generatedSourceDirs, swiftExportVersion)

        return KmpNativeCoroutinesModelImpl(
            compilerPluginOptions = compilerPluginOptions,
            swiftExportVersion = swiftExportVersion,
        )
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun getErrorMessageBuilder(project: Project, e: Exception): ErrorMessageBuilder =
        ErrorMessageBuilder.create(project, e, "Gradle import errors")
            .withDescription("Unable to build KMP-NativeCoroutines plugin configuration")
}

internal fun Any.readCompilerPluginOptions(
    generatedSourceDirs: List<String>,
): List<KmpNativeCoroutinesCompilerPluginOption>? {
    val contractClass = try {
        javaClass.classLoader.loadClass(CONFIGURATION_CONTRACT_CLASS)
    } catch (_: ClassNotFoundException) {
        return null
    }
    val contractMethod = contractClass.declaredMethods.firstOrNull { method ->
        method.name == CONFIGURATION_CONTRACT_METHOD &&
            method.parameterCount == 2 &&
            method.parameterTypes[0].isAssignableFrom(javaClass) &&
            List::class.java.isAssignableFrom(method.parameterTypes[1])
    } ?: return null

    val optionValues = contractMethod.invoke(null, this, generatedSourceDirs)
    check(optionValues is Map<*, *>) {
        "KMP-NativeCoroutines configuration contract must return a Map"
    }
    return buildList {
        for ((key, values) in optionValues) {
            check(key is String && values is List<*>) {
                "KMP-NativeCoroutines configuration contract contains an invalid entry"
            }
            for (value in values) {
                check(value is String) {
                    "KMP-NativeCoroutines configuration contract contains a non-string value"
                }
                add(KmpNativeCoroutinesCompilerPluginOption(key, value))
            }
        }
    }
}

internal fun Any.readLegacyCompilerPluginOptions(
    generatedSourceDirs: List<String>,
    swiftExportVersion: Long,
): List<KmpNativeCoroutinesCompilerPluginOption> {
    val extension = this
    return buildList {
        add(EXPOSED_SEVERITY, extension.readField<Enum<*>>(EXPOSED_SEVERITY.optionName)?.name
            ?: EXPOSED_SEVERITY.defaultValue.name)
        addAll(GENERATED_SOURCE_DIR, generatedSourceDirs)
        add(SUFFIX, extension.readField<String>(SUFFIX.optionName) ?: SUFFIX.defaultValue)
        extension.readField<String?>(FLOW_VALUE_SUFFIX.optionName)?.let { add(FLOW_VALUE_SUFFIX, it) }
        extension.readField<String?>(FLOW_REPLAY_CACHE_SUFFIX.optionName)?.let { add(FLOW_REPLAY_CACHE_SUFFIX, it) }
        add(STATE_SUFFIX, extension.readField<String>(STATE_SUFFIX.optionName) ?: STATE_SUFFIX.defaultValue)
        extension.readField<String?>(STATE_FLOW_SUFFIX.optionName)?.let { add(STATE_FLOW_SUFFIX, it) }
        val swiftExportMask = swiftExportVersion.takeIf {
            extension.readField<Boolean>("swiftExport") ?: false
        } ?: 0
        add(SWIFT_EXPORT, swiftExportMask.toString())
    }
}

private fun MutableList<KmpNativeCoroutinesCompilerPluginOption>.add(option: ConfigOption<*>, value: String) {
    add(KmpNativeCoroutinesCompilerPluginOption(option.optionName, value))
}

private fun MutableList<KmpNativeCoroutinesCompilerPluginOption>.addAll(
    option: ConfigListOption<*>,
    values: List<String>,
) {
    for (value in values) {
        add(KmpNativeCoroutinesCompilerPluginOption(option.optionName, value))
    }
}

private fun <T> Any.readField(fieldName: String, clazz: Class<*> = javaClass): T? {
    val field = clazz.declaredFields.firstOrNull { it.name == fieldName }
        ?: return readField(fieldName, clazz.superclass ?: return null)
    @Suppress("DEPRECATION")
    val isAccessible = field.isAccessible
    try {
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as? T
    } finally {
        field.isAccessible = isAccessible
    }
}
