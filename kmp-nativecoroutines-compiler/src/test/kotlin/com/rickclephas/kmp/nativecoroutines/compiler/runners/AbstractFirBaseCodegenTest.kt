package com.rickclephas.kmp.nativecoroutines.compiler.runners

import com.rickclephas.kmp.nativecoroutines.compiler.services.KmpNativeCoroutinesCompilerPluginConfigurator
import com.rickclephas.kmp.nativecoroutines.compiler.services.KmpNativeCoroutinesJvmRuntimeClasspathProvider
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.FirParser
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.handlers.IrPrettyKotlinDumpHandler
import org.jetbrains.kotlin.test.backend.ir.IrBackendInput
import org.jetbrains.kotlin.test.backend.ir.IrConstCheckerHandler
import org.jetbrains.kotlin.test.backend.ir.IrDiagnosticsHandler
import org.jetbrains.kotlin.test.backend.ir.JvmIrBackendFacade
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.builders.configureFirHandlersStep
import org.jetbrains.kotlin.test.builders.configureIrHandlersStep
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_IR
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.DUMP_KT_IR
import org.jetbrains.kotlin.test.directives.CodegenTestDirectives.IGNORE_DEXING
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.directives.FirDiagnosticsDirectives.FIR_DUMP
import org.jetbrains.kotlin.test.directives.JvmEnvironmentConfigurationDirectives.FULL_JDK
import org.jetbrains.kotlin.test.directives.configureFirParser
import org.jetbrains.kotlin.test.frontend.fir.Fir2IrResultsConverter
import org.jetbrains.kotlin.test.frontend.fir.FirFrontendFacade
import org.jetbrains.kotlin.test.frontend.fir.FirMetaInfoDiffSuppressor
import org.jetbrains.kotlin.test.frontend.fir.FirOutputArtifact
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirCfgDumpHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirDumpHandler
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirResolvedTypesVerifier
import org.jetbrains.kotlin.test.frontend.fir.handlers.FirScopeDumpHandler
import org.jetbrains.kotlin.test.initIdeaConfiguration
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.runners.codegen.AbstractJvmBlackBoxCodegenTestBase
import org.jetbrains.kotlin.test.runners.codegen.configureDumpHandlersForCodegenTest
import org.jetbrains.kotlin.test.services.EnvironmentBasedStandardLibrariesPathProvider
import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider
import org.junit.jupiter.api.BeforeAll

abstract class AbstractFirBaseCodegenTest(
    private val firParser: FirParser
): AbstractJvmBlackBoxCodegenTestBase<FirOutputArtifact, IrBackendInput>(
    FrontendKinds.FIR, TargetBackend.JVM_IR
) {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            initIdeaConfiguration()
        }
    }

    override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
        return EnvironmentBasedStandardLibrariesPathProvider
    }

    final override val frontendFacade: Constructor<FrontendFacade<FirOutputArtifact>>
        get() = ::FirFrontendFacade
    final override val frontendToBackendConverter: Constructor<Frontend2BackendConverter<FirOutputArtifact, IrBackendInput>>
        get() = ::Fir2IrResultsConverter
    final override val backendFacade: Constructor<BackendFacade<IrBackendInput, BinaryArtifacts.Jvm>>
        get() = ::JvmIrBackendFacade

    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        super.configure(builder)
        defaultDirectives {
            +WITH_STDLIB
            +FULL_JDK
            +FIR_DUMP
            +DUMP_IR
            +DUMP_KT_IR
            +IGNORE_DEXING
        }
        configureFirParser(firParser)
        configureFirHandlersStep {
            useHandlersAtFirst(
                ::FirDumpHandler,
                ::FirScopeDumpHandler,
                ::FirCfgDumpHandler,
                ::FirResolvedTypesVerifier,
            )
        }
        configureIrHandlersStep {
            useHandlers(
                ::IrDiagnosticsHandler,
                ::IrConstCheckerHandler,
            )
            useHandlers(
                ::IrPrettyKotlinDumpHandler,
            )
        }
        useAfterAnalysisCheckers(
            ::FirMetaInfoDiffSuppressor,
        )
        configureDumpHandlersForCodegenTest()
        useConfigurators(::KmpNativeCoroutinesCompilerPluginConfigurator)
        useCustomRuntimeClasspathProviders(::KmpNativeCoroutinesJvmRuntimeClasspathProvider)
    }
}

open class AbstractFirPsiCodegenTest: AbstractFirBaseCodegenTest(FirParser.Psi)
open class AbstractFirLightTreeCodegenTest: AbstractFirBaseCodegenTest(FirParser.LightTree)
