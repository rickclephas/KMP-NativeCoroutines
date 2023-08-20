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
     * The suffix used to generate the native state property names.
     * @see com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
     */
    public var stateSuffix: String = "Value"
    /**
     * The suffix used to generate the `StateFlow` flow property names,
     * or `null` to remove the flow properties.
     * @see com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
     */
    public var stateFlowSuffix: String? = "Flow"
    /**
     * The severity of the exposed coroutines check.
     */
    public var exposedSeverity: ExposedSeverity = ExposedSeverity.WARNING
}

public enum class ExposedSeverity {
    NONE, WARNING, ERROR
}
