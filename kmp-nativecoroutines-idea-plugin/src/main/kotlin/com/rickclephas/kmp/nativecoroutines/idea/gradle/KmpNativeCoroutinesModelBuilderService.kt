package com.rickclephas.kmp.nativecoroutines.idea.gradle

import org.gradle.api.Project
import org.jetbrains.plugins.gradle.tooling.AbstractModelBuilderService
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderContext
import java.lang.Exception

public class KmpNativeCoroutinesModelBuilderService: AbstractModelBuilderService() {

    override fun canBuild(modelName: String?): Boolean = modelName == KmpNativeCoroutinesModel::class.java.name

    override fun buildAll(modelName: String, project: Project, context: ModelBuilderContext): KmpNativeCoroutinesModel? {
        if (project.plugins.findPlugin("com.rickclephas.kmp.nativecoroutines") == null) return null
        val extension = project.extensions.findByName("nativeCoroutines") ?: return null
        return KmpNativeCoroutinesModelImpl(
            exposedSeverity = extension.get<Enum<*>>("exposedSeverity")?.name ?: "WARNING"
        )
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun getErrorMessageBuilder(project: Project, e: Exception): ErrorMessageBuilder =
        ErrorMessageBuilder.create(project, e, "Gradle import errors")
            .withDescription("Unable to build KMP-NativeCoroutines plugin configuration")

    private operator fun <T> Any.get(fieldName: String, clazz: Class<*> = this.javaClass): T? {
        val field = clazz.declaredFields.firstOrNull { it.name == fieldName }
            ?: return get(fieldName, clazz.superclass ?: return null)
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
}
