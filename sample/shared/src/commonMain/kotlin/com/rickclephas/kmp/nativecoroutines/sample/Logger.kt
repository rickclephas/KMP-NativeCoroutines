package com.rickclephas.kmp.nativecoroutines.sample

enum class Severity {
    Verbose, Debug, Info, Warn, Error, Assert
}

abstract class Logger {
    open fun isLoggable(severity: Severity): Boolean = true

    abstract fun log(severity: Severity, message: String, tag: String, throwable: Throwable? = null)

    open fun v(message: String, tag: String, throwable: Throwable? = null): Unit =
        log(Severity.Verbose, message, tag, throwable)

    open fun d(message: String, tag: String, throwable: Throwable? = null): Unit =
        log(Severity.Debug, message, tag, throwable)

    open fun i(message: String, tag: String, throwable: Throwable? = null): Unit =
        log(Severity.Info, message, tag, throwable)

    open fun w(message: String, tag: String, throwable: Throwable? = null): Unit =
        log(Severity.Warn, message, tag, throwable)

    open fun e(message: String, tag: String, throwable: Throwable? = null): Unit =
        log(Severity.Error, message, tag, throwable)

    open fun wtf(message: String, tag: String, throwable: Throwable? = null): Unit =
        log(Severity.Assert, message, tag, throwable)
}