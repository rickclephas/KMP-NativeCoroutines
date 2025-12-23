package com.rickclephas.kmp.nativecoroutines.idea.gradle

import com.intellij.openapi.externalSystem.model.Key
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

internal class KmpNativeCoroutinesModelImpl(
    override val suffix: String,
    override val flowValueSuffix: String?,
    override val flowReplayCacheSuffix: String?,
    override val stateSuffix: String,
    override val stateFlowSuffix: String?,
    override val exposedSeverity: String,
    override val generatedSourceDirs: List<String>,
    override val swiftExport: Boolean,
    override val swiftExportVersion: Long,
): KmpNativeCoroutinesModel

internal val KmpNativeCoroutinesModelKey = Key<KmpNativeCoroutinesModel>(KmpNativeCoroutinesModel::class.java.name, 1)
