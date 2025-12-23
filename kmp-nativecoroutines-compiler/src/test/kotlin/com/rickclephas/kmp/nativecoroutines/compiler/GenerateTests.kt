package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.runners.*
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

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

            testClass<AbstractFirPsiCodegenTest> {
                model("codegen", excludedPattern = excludePattern)
            }
            testClass<AbstractFirLightTreeCodegenTest> {
                model("codegen", excludedPattern = excludePattern)
            }
        }
    }
}
