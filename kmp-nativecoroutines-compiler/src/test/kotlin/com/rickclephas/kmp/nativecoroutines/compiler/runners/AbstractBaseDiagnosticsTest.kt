package com.rickclephas.kmp.nativecoroutines.compiler.runners

import com.rickclephas.kmp.nativecoroutines.compiler.services.KmpNativeCoroutinesCompilerPluginConfigurator
import com.rickclephas.kmp.nativecoroutines.compiler.services.KmpNativeCoroutinesNativeRuntimeClasspathProvider
import org.jetbrains.kotlin.platform.konan.NativePlatforms
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.directives.NativeEnvironmentConfigurationDirectives.WITH_PLATFORM_LIBS
import org.jetbrains.kotlin.test.frontend.classic.handlers.OldNewInferenceMetaInfoProcessor
import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.services.configuration.CommonEnvironmentConfigurator
import org.jetbrains.kotlin.test.services.configuration.NativeEnvironmentConfigurator
import org.junit.jupiter.api.BeforeAll

abstract class AbstractBaseDiagnosticsTest<R : ResultingArtifact.FrontendOutput<R>>: AbstractKotlinCompilerTest() {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            initIdeaConfiguration()
        }
    }

    abstract val targetFrontend: FrontendKind<R>
    abstract val frontend: Constructor<FrontendFacade<R>>
    abstract fun TestConfigurationBuilder.handlersSetup()

    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        globalDefaults {
            frontend = targetFrontend
            targetPlatform = NativePlatforms.unspecifiedNativePlatform
            dependencyKind = DependencyKind.Source
        }
        defaultDirectives {
            +WITH_STDLIB
            +WITH_PLATFORM_LIBS
        }
        enableMetaInfoHandler()
        useConfigurators(::CommonEnvironmentConfigurator, ::NativeEnvironmentConfigurator)
        useMetaInfoProcessors(::OldNewInferenceMetaInfoProcessor)
        facadeStep(frontend)
        handlersSetup()
        useConfigurators(::KmpNativeCoroutinesCompilerPluginConfigurator)
        useCustomRuntimeClasspathProviders(::KmpNativeCoroutinesNativeRuntimeClasspathProvider)
    }
}
