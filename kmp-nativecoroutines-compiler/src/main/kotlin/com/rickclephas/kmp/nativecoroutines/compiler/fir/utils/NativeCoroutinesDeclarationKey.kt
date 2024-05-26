package com.rickclephas.kmp.nativecoroutines.compiler.fir.utils

import com.rickclephas.kmp.nativecoroutines.compiler.utils.CallableSignature
import org.jetbrains.kotlin.GeneratedDeclarationKey

internal data class NativeCoroutinesDeclarationKey(
    val type: Type,
    val callableSignature: CallableSignature
): GeneratedDeclarationKey() {
    enum class Type {
        NATIVE, STATE_FLOW_VALUE, SHARED_FLOW_REPLAY_CACHE
    }
}
