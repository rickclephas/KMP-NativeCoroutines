package com.rickclephas.kmp.nativecoroutines.compiler.runners

import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.firHandlersStep
import org.jetbrains.kotlin.test.configuration.configurationForClassicAndFirTestsAlongside
import org.jetbrains.kotlin.test.configuration.enableLazyResolvePhaseChecking
import org.jetbrains.kotlin.test.directives.configureFirParser
import org.jetbrains.kotlin.test.frontend.fir.FirFrontendFacade
import org.jetbrains.kotlin.test.frontend.fir.FirOutputArtifact
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirDiagnosticsHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirResolvedTypesVerifier
import org.jetbrains.kotlin.test.model.*

abstract class AbstractFirBaseDiagnosticsTest(
    private val parser: FirParser
): AbstractBaseDiagnosticsTest<FirOutputArtifact>() {
    final override val targetFrontend: FrontendKind<FirOutputArtifact>
        get() = FrontendKinds.FIR
    final override val frontend: Constructor<FrontendFacade<FirOutputArtifact>>
        get() = ::FirFrontendFacade
    final override fun TestConfigurationBuilder.handlersSetup() = firHandlersStep {
        useHandlers(::FirDiagnosticsHandler, ::FirResolvedTypesVerifier)
    }

    override fun configure(builder: TestConfigurationBuilder) {
        super.configure(builder)
        with(builder) {
            configureFirParser(parser)
            enableLazyResolvePhaseChecking()
            forTestsMatching("testData/diagnostics/*") {
                configurationForClassicAndFirTestsAlongside()
            }
        }
    }
}

abstract class AbstractFirPsiDiagnosticsTest: AbstractFirBaseDiagnosticsTest(FirParser.Psi)
abstract class AbstractFirLightTreeDiagnosticsTest: AbstractFirBaseDiagnosticsTest(FirParser.LightTree)
