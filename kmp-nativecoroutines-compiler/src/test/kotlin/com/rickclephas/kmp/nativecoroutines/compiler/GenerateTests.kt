package com.rickclephas.kmp.nativecoroutines.compiler

import com.rickclephas.kmp.nativecoroutines.compiler.runners.AbstractDiagnosticsTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "src/testData", testsRoot = "src/test/generated") {
            testClass<AbstractDiagnosticsTest> {
                model("diagnostics")
            }
        }
    }
}
