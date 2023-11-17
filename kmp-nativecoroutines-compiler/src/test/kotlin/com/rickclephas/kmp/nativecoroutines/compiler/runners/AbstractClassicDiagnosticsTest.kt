package com.rickclephas.kmp.nativecoroutines.compiler.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.classicFrontendHandlersStep
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontendFacade
import org.jetbrains.kotlin.test.frontend.classic.ClassicFrontendOutputArtifact
import org.jetbrains.kotlin.test.frontend.classic.handlers.ClassicDiagnosticsHandler
import org.jetbrains.kotlin.test.model.*

abstract class AbstractClassicDiagnosticsTest: AbstractBaseDiagnosticsTest<ClassicFrontendOutputArtifact>() {
    final override val targetFrontend: FrontendKind<ClassicFrontendOutputArtifact>
        get() = FrontendKinds.ClassicFrontend
    final override val frontend: Constructor<FrontendFacade<ClassicFrontendOutputArtifact>>
        get() = ::ClassicFrontendFacade
    final override fun TestConfigurationBuilder.handlersSetup() = classicFrontendHandlersStep {
         useHandlers(::ClassicDiagnosticsHandler)
    }
}
