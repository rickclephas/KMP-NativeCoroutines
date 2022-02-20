//
//  CompilerIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/08/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import NativeCoroutinesSampleShared

class CompilerIntegrationTests: XCTestCase {
    
    private typealias IntegrationTests = NativeCoroutinesSampleShared.CompilerIntegrationTests<NSNumber>
    private let testExceptionMessage = "com.rickclephas.kmp.nativecoroutines.sample.utils.TestException"
    private let classTestExceptionMessage = "com.rickclephas.kmp.nativecoroutines.sample.utils.ClassTestException"
    private let moduleTestExceptionMessage = "com.rickclephas.kmp.nativecoroutines.sample.utils.ModuleTestException"
    
    func testThrowWithThrows() {
        let integrationTests = IntegrationTests()
        let errorExpectation = expectation(description: "Waiting for error")
        _ = integrationTests.throwWithThrowsNative()({ _, unit in unit}, { error, unit in
            let error = error as NSError
            let exception = error.userInfo["KotlinException"] as! KotlinThrowable
            XCTAssertEqual(exception.message, self.testExceptionMessage, "Received incorrect exception")
            errorExpectation.fulfill()
            return unit
        })
        wait(for: [errorExpectation], timeout: 2)
    }
    
    func testThrowWithNativeCoroutineThrows() {
        let integrationTests = IntegrationTests()
        let errorExpectation = expectation(description: "Waiting for error")
        _ = integrationTests.throwWithNativeCoroutineThrowsNative()({ _, unit in unit}, { error, unit in
            let error = error as NSError
            let exception = error.userInfo["KotlinException"] as! KotlinThrowable
            XCTAssertEqual(exception.message, self.testExceptionMessage, "Received incorrect exception")
            errorExpectation.fulfill()
            return unit
        })
        wait(for: [errorExpectation], timeout: 2)
    }
    
    func testThrowWithNativeCoroutineThrowsOnClass() {
        let integrationTests = IntegrationTests()
        let errorExpectation = expectation(description: "Waiting for error")
        _ = integrationTests.throwWithNativeCoroutineThrowsOnClassNative()({ _, unit in unit}, { error, unit in
            let error = error as NSError
            let exception = error.userInfo["KotlinException"] as! KotlinThrowable
            XCTAssertEqual(exception.message, self.classTestExceptionMessage, "Received incorrect exception")
            errorExpectation.fulfill()
            return unit
        })
        wait(for: [errorExpectation], timeout: 2)
    }
    
    func testThrowWithPropagatedExceptionInModule() {
        let integrationTests = IntegrationTests()
        let errorExpectation = expectation(description: "Waiting for error")
        _ = integrationTests.throwWithPropagatedExceptionInModuleNative()({ _, unit in unit}, { error, unit in
            let error = error as NSError
            let exception = error.userInfo["KotlinException"] as! KotlinThrowable
            XCTAssertEqual(exception.message, self.moduleTestExceptionMessage, "Received incorrect exception")
            errorExpectation.fulfill()
            return unit
        })
        wait(for: [errorExpectation], timeout: 2)
    }
    
    func testFlowThrow() {
        let integrationTests = IntegrationTests()
        let errorExpectation = expectation(description: "Waiting for error")
        _ = integrationTests.flowThrowNative({ _, unit in unit}, { error, unit in
            let error = error! as NSError
            let exception = error.userInfo["KotlinException"] as! KotlinThrowable
            XCTAssertEqual(exception.message, self.testExceptionMessage, "Received incorrect exception")
            errorExpectation.fulfill()
            return unit
        })
        wait(for: [errorExpectation], timeout: 2)
    }
    
    func testStateFlowValue() {
        let integrationTests = IntegrationTests()
        let value = integrationTests.stateFlowNativeValue
        XCTAssertEqual(value, 1, "Received inccorect value")
    }
    
    func testSharedFlowReplayCache() {
        let integrationTests = IntegrationTests()
        let replayCache = integrationTests.sharedFlowNativeReplayCache
        XCTAssertEqual(replayCache, [1, 2], "Received inccorect value")
    }
    
    func testReturnGenericClassValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericClassValueNative(value: sendValue)({ value, unit in
            XCTAssertEqual(value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnDefaultValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = randomInt()
        _ = integrationTests.returnDefaultValueNative(value: sendValue)({ value, unit in
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericValueNative(value: sendValue)({ value, unit in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnConstrainedGenericValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = integrationTests.returnAppendable(value: randomString())
        _ = integrationTests.returnConstrainedGenericValueNative(value: sendValue)({ value, unit in
            XCTAssertIdentical(value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericValues() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for values")
        let sendValues = [NSNumber(value: randomInt()), NSNumber(value: randomInt())]
        _ = integrationTests.returnGenericValuesNative(values: sendValues)({ values, unit in
            XCTAssertEqual(values as! [NSNumber], sendValues, "Received incorrect values")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericVarargValues() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for values")
        let sendValues = KotlinArray<AnyObject>(size: 2) { _ in
            NSNumber(value: self.randomInt())
        }
        _ = integrationTests.returnGenericVarargValuesNative(values: sendValues)({ values, unit in
            XCTAssertEqual(values, sendValues, "Received incorrect values")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericValueFromExtension() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericValueFromExtensionNative([], value: sendValue)({ value, unit in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericFlow() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericFlowNative(value: sendValue)({ value, unit in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
}
