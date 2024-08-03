package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.externalSystem.model.Key
import java.io.Serializable

public interface KmpNativeCoroutinesModel: Serializable {
    public val exposedSeverity: String
    public val generatedSourceDirs: List<String>
    public val k2Mode: Boolean
}

internal class KmpNativeCoroutinesModelImpl(
    override val exposedSeverity: String,
    override val generatedSourceDirs: List<String>,
    override val k2Mode: Boolean,
): KmpNativeCoroutinesModel

internal val KmpNativeCoroutinesModelKey = Key<KmpNativeCoroutinesModel>(KmpNativeCoroutinesModel::class.java.name, 1)
