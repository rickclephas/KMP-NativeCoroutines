package com.rickclephas.kmp.nativecoroutines.compiler.services

import com.rickclephas.kmp.nativecoroutines.compiler.KmpNativeCoroutinesCompilerPluginRegistrar
import com.rickclephas.kmp.nativecoroutines.compiler.config.KmpNativeCoroutinesConfigurationKeys as ConfigurationKeys
import com.rickclephas.kmp.nativecoroutines.compiler.directives.KmpNativeCoroutinesDirectives as Directives
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.directives.model.DirectivesContainer
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.DirectiveToConfigurationKeyExtractor
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices

@OptIn(ExperimentalCompilerApi::class)
internal class KmpNativeCoroutinesCompilerPluginConfigurator(
    testServices: TestServices
): EnvironmentConfigurator(testServices) {

    override val directiveContainers: List<DirectivesContainer> = listOf(Directives)

    override fun DirectiveToConfigurationKeyExtractor.provideConfigurationKeys() {
        register(Directives.EXPOSED_SEVERITY, ConfigurationKeys.EXPOSED_SEVERITY)
    }

    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration
    ) = with(KmpNativeCoroutinesCompilerPluginRegistrar()) { registerExtensions(configuration) }
}
