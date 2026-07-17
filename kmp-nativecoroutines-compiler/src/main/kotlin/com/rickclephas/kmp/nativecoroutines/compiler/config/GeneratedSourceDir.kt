package com.rickclephas.kmp.nativecoroutines.compiler.config

import java.nio.file.Path
import kotlin.io.path.Path

public val GENERATED_SOURCE_DIR: ConfigListOption<Path> =
    object : ConfigListOption<Path>("generatedSourceDir") {
        override val description: String = "Specifies a directory containing generated sources"
        override val valueDescription: String = "Directory path"
        override fun parse(value: String): Path = Path(value)
    }

/**
 * Checks if this file path is located in the generated source [dir].
 *
 * Absolute [dir]s must contain this path,
 * while relative [dir]s are matched as a contiguous subpath.
 * Relative paths are preferred since they keep the compiler arguments,
 * and with that the Gradle build cache keys, independent of the project location.
 */
public fun Path.isLocatedIn(dir: Path): Boolean {
    if (dir.isAbsolute) return startsWith(dir)
    return (0..(nameCount - dir.nameCount)).any { subpath(it, it + dir.nameCount) == dir }
}
