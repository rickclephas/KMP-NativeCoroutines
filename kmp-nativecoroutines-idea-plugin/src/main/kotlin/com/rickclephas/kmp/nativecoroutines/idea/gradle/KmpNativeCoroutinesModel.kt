package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.util.Key
import java.io.Serializable

interface KmpNativeCoroutinesModel: Serializable {
    val exposedSeverity: String
}

internal class KmpNativeCoroutinesModelImpl(
    override val exposedSeverity: String
): KmpNativeCoroutinesModel {
    companion object {
        val KEY = Key<KmpNativeCoroutinesModel>(KmpNativeCoroutinesModel::class.java.name)
    }
}
