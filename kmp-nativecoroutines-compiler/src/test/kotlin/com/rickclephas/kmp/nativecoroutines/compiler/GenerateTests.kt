package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractClassicDiagnosticsTest
import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractFirLightTreeDiagnosticsTest
import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractFirPsiDiagnosticsTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "src/testData", testsRoot = "src/test/generated") {
            val excludePattern = "^(.+)\\.fir\\.kt\$"
            testClass<AbstractClassicDiagnosticsTest> {
                model("diagnostics", excludedPattern = excludePattern)
            }
            testClass<AbstractFirPsiDiagnosticsTest> {
                model("diagnostics", excludedPattern = excludePattern)
            }
            testClass<AbstractFirLightTreeDiagnosticsTest> {
                model("diagnostics", excludedPattern = excludePattern)
            }
        }
    }
}
