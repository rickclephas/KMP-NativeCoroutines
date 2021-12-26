package com.rickclephas.kmp.nativecoroutines.compiler

import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.name.FqName

internal const val SUFFIX_OPTION_NAME = "suffix"
internal val SUFFIX_KEY = CompilerConfigurationKey<String>(SUFFIX_OPTION_NAME)

internal const val PROPAGATED_EXCEPTIONS_OPTION_NAME = "propagatedExceptions"
internal val PROPAGATED_EXCEPTIONS_KEY = CompilerConfigurationKey<List<FqName>>(PROPAGATED_EXCEPTIONS_OPTION_NAME)

internal const val USE_THROWS_ANNOTATION_OPTION_NAME = "useThrowsAnnotation"
internal val USE_THROWS_ANNOTATION_KEY = CompilerConfigurationKey<Boolean>(USE_THROWS_ANNOTATION_OPTION_NAME)
