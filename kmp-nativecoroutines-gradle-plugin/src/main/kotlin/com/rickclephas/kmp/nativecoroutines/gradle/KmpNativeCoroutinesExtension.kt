package com.rickclephas.kmp.nativecoroutines.gradle

public open class KmpNativeCoroutinesExtension {
    /**
     * The suffix used to generate the native coroutine function and property names.
     */
    public var suffix: String = "Native"
    /**
     * The suffix used to generate the native coroutine file names.
     * Note: defaults to [suffix] when `null`.
     */
    public var fileSuffix: String? = null
    /**
     * The suffix used to generate the `StateFlow` value property names,
     * or `null` to remove the value properties.
     */
    public var flowValueSuffix: String? = "Value"
    /**
     * The suffix used to generate the `SharedFlow` replayCache property names,
     * or `null` to remove the replayCache properties.
     */
    public var flowReplayCacheSuffix: String? = "ReplayCache"
    /**
     * The suffix used to generate the `StateFlow` value property names.
     */
    public var stateSuffix: String = "Value"
    /**
     * The suffix used to generate the `StateFlow` flow property names,
     * or `null` to remove the flow properties.
     */
    public var stateFlowSuffix: String? = "Flow"
    /**
     * The severity of the exposed coroutines check.
     */
    public var exposedSeverity: ExposedSeverity = ExposedSeverity.WARNING
    /**
     * A list of generated source directories.
     */
    public val generatedSourceDirs: MutableList<Any> = mutableListOf("build/generated")
    /**
     * Indicates if the plugin should run in Swift export compatibility mode.
     */
    public var swiftExport: Boolean = false
    /**
     * The compatibility version of Swift export used by the plugin.
     */
    public val swiftExportVersion: Long = 0b11
}

public enum class ExposedSeverity {
    NONE, WARNING, ERROR
}
