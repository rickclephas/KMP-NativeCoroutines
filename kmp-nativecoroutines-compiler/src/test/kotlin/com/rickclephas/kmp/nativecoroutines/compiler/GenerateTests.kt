package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractClassicDiagnosticsTest
import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractFirLightTreeDiagnosticsTest
import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractFirPsiDiagnosticsTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "src/testData", testsRoot = "src/test/generated") {
            testClass<AbstractClassicDiagnosticsTest> {
                model("diagnostics")
            }
            testClass<AbstractFirPsiDiagnosticsTest> {
                model("diagnostics")
            }
            testClass<AbstractFirLightTreeDiagnosticsTest> {
                model("diagnostics")
            }
        }
    }
}
