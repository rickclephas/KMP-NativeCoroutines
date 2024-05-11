package com.rickclephas.kmp.nativecoroutines.compiler.config

import java.nio.file.Path
import kotlin.io.path.Path

public val GENERATED_SOURCE_DIR: ConfigListOption<Path> =
    object : ConfigListOption<Path>("generatedSourceDir") {
        override val description: String = "Specifies a directory containing generated sources"
        override val valueDescription: String = "Directory path"
        override fun parse(value: String): Path = Path(value)
    }
