package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.util.Key
import java.io.Serializable

public interface KmpNativeCoroutinesModel: Serializable {
    public val exposedSeverity: String
    public val generatedSourceDirs: List<String>
}

internal class KmpNativeCoroutinesModelImpl(
    override val exposedSeverity: String,
    override val generatedSourceDirs: List<String>
): KmpNativeCoroutinesModel

internal val KmpNativeCoroutinesModelKey = Key<KmpNativeCoroutinesModel>(KmpNativeCoroutinesModel::class.java.name)
