package com.rickclephas.kmp.nativecoroutines.sample.tests

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
public class ThreadLockIntegrationTests: IntegrationTests() {

    override val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _stateFlow = MutableStateFlow(0)
    @NativeCoroutines
    public val stateFlow: StateFlow<Int> = _stateFlow.asStateFlow()

    init {
        GlobalScope.launch {
            while(true) {
                delay(1.seconds)
                _stateFlow.update { it + 1 }
            }
        }
    }
}
