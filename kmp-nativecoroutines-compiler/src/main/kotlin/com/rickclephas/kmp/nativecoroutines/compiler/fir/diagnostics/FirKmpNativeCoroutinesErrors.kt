package com.rickclephas.kmp.nativecoroutines.compiler.fir.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.psi.KtElement

public object FirKmpNativeCoroutinesErrors {
    public val CONFLICT_COROUTINES: KtDiagnosticFactory0 by error0<KtElement>()
    public val EXPOSED_FLOW_TYPE: KtDiagnosticFactory0 by warning0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
    public val EXPOSED_FLOW_TYPE_ERROR: KtDiagnosticFactory0 by error0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
    public val EXPOSED_STATE_FLOW_PROPERTY: KtDiagnosticFactory0 by warning0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
    public val EXPOSED_STATE_FLOW_PROPERTY_ERROR: KtDiagnosticFactory0 by error0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
    public val EXPOSED_SUSPEND_FUNCTION: KtDiagnosticFactory0 by warning0<KtElement>(SourceElementPositioningStrategies.SUSPEND_MODIFIER)
    public val EXPOSED_SUSPEND_FUNCTION_ERROR: KtDiagnosticFactory0 by error0<KtElement>(SourceElementPositioningStrategies.SUSPEND_MODIFIER)
    public val EXPOSED_SUSPEND_TYPE: KtDiagnosticFactory0 by warning0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
    public val EXPOSED_SUSPEND_TYPE_ERROR: KtDiagnosticFactory0 by error0<KtElement>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
    public val IGNORED_COROUTINES: KtDiagnosticFactory0 by error0<KtElement>()
    public val IGNORED_COROUTINES_REFINED: KtDiagnosticFactory0 by error0<KtElement>()
    public val IGNORED_COROUTINES_REFINED_STATE: KtDiagnosticFactory0 by error0<KtElement>()
    public val IGNORED_COROUTINES_STATE: KtDiagnosticFactory0 by error0<KtElement>()
    public val INVALID_COROUTINES: KtDiagnosticFactory0 by error0<KtElement>()
    public val INVALID_COROUTINES_IGNORE: KtDiagnosticFactory0 by error0<KtElement>()
    public val INVALID_COROUTINES_REFINED: KtDiagnosticFactory0 by error0<KtElement>()
    public val INVALID_COROUTINES_REFINED_STATE: KtDiagnosticFactory0 by error0<KtElement>()
    public val INVALID_COROUTINES_STATE: KtDiagnosticFactory0 by error0<KtElement>()
    public val INVALID_COROUTINE_SCOPE: KtDiagnosticFactory0 by error0<KtElement>()
    public val REDUNDANT_OVERRIDE_COROUTINES: KtDiagnosticFactory0 by warning0<KtElement>()
    public val REDUNDANT_OVERRIDE_COROUTINES_IGNORE: KtDiagnosticFactory0 by error0<KtElement>()
    public val REDUNDANT_OVERRIDE_COROUTINES_REFINED: KtDiagnosticFactory0 by warning0<KtElement>()
    public val REDUNDANT_OVERRIDE_COROUTINES_REFINED_STATE: KtDiagnosticFactory0 by warning0<KtElement>()
    public val REDUNDANT_OVERRIDE_COROUTINES_STATE: KtDiagnosticFactory0 by warning0<KtElement>()
    public val REDUNDANT_PRIVATE_COROUTINES: KtDiagnosticFactory0 by error0<KtElement>()
    public val REDUNDANT_PRIVATE_COROUTINES_IGNORE: KtDiagnosticFactory0 by error0<KtElement>()
    public val REDUNDANT_PRIVATE_COROUTINES_REFINED: KtDiagnosticFactory0 by error0<KtElement>()
    public val REDUNDANT_PRIVATE_COROUTINES_REFINED_STATE: KtDiagnosticFactory0 by error0<KtElement>()
    public val REDUNDANT_PRIVATE_COROUTINES_STATE: KtDiagnosticFactory0 by error0<KtElement>()
    public val UNSUPPORTED_CLASS_EXTENSION_PROPERTY: KtDiagnosticFactory0 by error0<KtElement>()

    init {
        RootDiagnosticRendererFactory.registerFactory(FirDefaultErrorMessages)
    }
}
