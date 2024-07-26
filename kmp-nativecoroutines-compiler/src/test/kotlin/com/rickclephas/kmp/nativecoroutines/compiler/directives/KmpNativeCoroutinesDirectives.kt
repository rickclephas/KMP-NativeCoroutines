package com.rickclephas.kmp.nativecoroutines.compiler.directives

import com.rickclephas.kmp.nativecoroutines.compiler.config.ConfigOption
import com.rickclephas.kmp.nativecoroutines.compiler.config.EXPOSED_SEVERITY as EXPOSED_SEVERITY_CONFIG
import com.rickclephas.kmp.nativecoroutines.compiler.config.SUFFIX as SUFFIX_CONFIG
import com.rickclephas.kmp.nativecoroutines.compiler.config.FLOW_VALUE_SUFFIX as FLOW_VALUE_SUFFIX_CONFIG
import com.rickclephas.kmp.nativecoroutines.compiler.config.FLOW_REPLAY_CACHE_SUFFIX as FLOW_REPLAY_CACHE_SUFFIX_CONFIG
import com.rickclephas.kmp.nativecoroutines.compiler.config.STATE_SUFFIX as STATE_SUFFIX_CONFIG
import com.rickclephas.kmp.nativecoroutines.compiler.config.STATE_FLOW_SUFFIX as STATE_FLOW_SUFFIX_CONFIG
import com.rickclephas.kmp.nativecoroutines.compiler.config.K2_MODE as K2_MODE_CONFIG
import org.jetbrains.kotlin.test.directives.model.DirectiveApplicability
import org.jetbrains.kotlin.test.directives.model.SimpleDirective
import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.directives.model.ValueDirective

internal object KmpNativeCoroutinesDirectives: SimpleDirectivesContainer() {

    private fun directive(
        configOption: ConfigOption<Boolean>,
        applicability: DirectiveApplicability = DirectiveApplicability.Global
    ): DirectiveDelegateProvider<SimpleDirective> = directive(configOption.description, applicability)

    private fun <T : Any> valueDirective(
        configOption: ConfigOption<T>,
        applicability: DirectiveApplicability = DirectiveApplicability.Global
    ): DirectiveDelegateProvider<ValueDirective<T>> = valueDirective(configOption.description, applicability, configOption::parse)

    val EXPOSED_SEVERITY by valueDirective(EXPOSED_SEVERITY_CONFIG)
    val SUFFIX by valueDirective(SUFFIX_CONFIG)
    val FLOW_VALUE_SUFFIX by valueDirective(FLOW_VALUE_SUFFIX_CONFIG)
    val FLOW_REPLAY_CACHE_SUFFIX by valueDirective(FLOW_REPLAY_CACHE_SUFFIX_CONFIG)
    val STATE_SUFFIX by valueDirective(STATE_SUFFIX_CONFIG)
    val STATE_FLOW_SUFFIX by valueDirective(STATE_FLOW_SUFFIX_CONFIG)
    val K2_MODE by directive(K2_MODE_CONFIG)
}
