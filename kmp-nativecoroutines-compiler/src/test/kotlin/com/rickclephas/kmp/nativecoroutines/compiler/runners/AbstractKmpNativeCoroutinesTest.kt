package com.rickclephas.kmp.nativecoroutines.compiler.runners

import com.rickclephas.kmp.nativecoroutines.compiler.services.HelpersSourceProvider
import com.rickclephas.kmp.nativecoroutines.compiler.services.KmpNativeCoroutinesCompilerPluginConfigurator
import org.jetbrains.kotlin.test.bind
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.junit.jupiter.api.BeforeAll

abstract class AbstractKmpNativeCoroutinesTest: AbstractKotlinCompilerTest() {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            initIdeaConfiguration()
        }
    }

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        with(builder) {
            useConfigurators(::KmpNativeCoroutinesCompilerPluginConfigurator)
            useAdditionalSourceProviders(::HelpersSourceProvider.bind(setOf(
                HelpersSourceProvider.kmpNativeCoroutinesAnnotations,
                HelpersSourceProvider.kotlinNative,
                HelpersSourceProvider.kotlinxCoroutinesCore
            )))
        }
    }
}
