package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.externalSystem.model.Key
import java.io.Serializable

public interface KmpNativeCoroutinesModel: Serializable {
    public val exposedSeverity: String
}

internal class KmpNativeCoroutinesModelImpl(
    override val exposedSeverity: String
): KmpNativeCoroutinesModel

internal val KmpNativeCoroutinesModelKey = Key<KmpNativeCoroutinesModel>(KmpNativeCoroutinesModel::class.java.name, 1)
