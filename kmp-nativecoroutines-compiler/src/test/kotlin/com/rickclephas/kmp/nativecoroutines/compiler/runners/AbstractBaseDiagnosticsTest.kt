package com.rickclephas.kmp.nativecoroutines.compiler.runners

import org.jetbrains.kotlin.platform.konan.NativePlatforms
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives
import org.jetbrains.kotlin.test.frontend.classic.handlers.OldNewInferenceMetaInfoProcessor
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.services.configuration.CommonEnvironmentConfigurator
import org.jetbrains.kotlin.test.services.configuration.NativeEnvironmentConfigurator

abstract class AbstractBaseDiagnosticsTest<R : ResultingArtifact.FrontendOutput<R>>: AbstractKmpNativeCoroutinesTest() {
    abstract val targetFrontend: FrontendKind<R>
    abstract val frontend: Constructor<FrontendFacade<R>>
    abstract fun TestConfigurationBuilder.handlersSetup()

    final override fun TestConfigurationBuilder.configuration() {
        globalDefaults {
            frontend = targetFrontend
            targetPlatform = NativePlatforms.unspecifiedNativePlatform
            dependencyKind = DependencyKind.Source
        }
        defaultDirectives {
            +ConfigurationDirectives.WITH_STDLIB
        }
        enableMetaInfoHandler()
        useConfigurators(::CommonEnvironmentConfigurator, ::NativeEnvironmentConfigurator)
        useMetaInfoProcessors(::OldNewInferenceMetaInfoProcessor)
        facadeStep(frontend)
        handlersSetup()
    }
}
