package com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics

import com.rickclephas.kmp.nativecoroutines.compiler.classic.diagnostics.CustomPositioningStrategies
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategy

internal object CustomSourceElementPositioningStrategies {
    val DECLARATION_RETURN_TYPE = SourceElementPositioningStrategy(
        CustomLightTreePositioningStrategies.DECLARATION_RETURN_TYPE,
        CustomPositioningStrategies.DECLARATION_RETURN_TYPE
    )
}
