package com.rickclephas.kmp.nativecoroutines.compiler.runners

import org.jetbrains.kotlin.platform.konan.NativePlatforms
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.classicFrontendHandlersStep
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontendFacade
import org.jetbrains.kotlin.test.frontend.classic.handlers.ClassicDiagnosticsHandler
import org.jetbrains.kotlin.test.frontend.classic.handlers.OldNewInferenceMetaInfoProcessor
import org.jetbrains.kotlin.test.model.DependencyKind
import org.jetbrains.kotlin.test.model.FrontendKinds
import org.jetbrains.kotlin.test.services.configuration.CommonEnvironmentConfigurator

abstract class AbstractDiagnosticsTest: AbstractKmpNativeCoroutinesTest() {

    override fun TestConfigurationBuilder.configuration() {
        globalDefaults {
            frontend = FrontendKinds.ClassicFrontend
            targetPlatform = NativePlatforms.unspecifiedNativePlatform
            dependencyKind = DependencyKind.Source
        }
        enableMetaInfoHandler()
        useConfigurators(::CommonEnvironmentConfigurator)
        useMetaInfoProcessors(::OldNewInferenceMetaInfoProcessor)
        facadeStep(::ClassicFrontendFacade)
        classicFrontendHandlersStep {
            useHandlers(::ClassicDiagnosticsHandler)
        }
    }
}
