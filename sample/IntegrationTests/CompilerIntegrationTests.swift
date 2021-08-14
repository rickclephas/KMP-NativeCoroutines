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
    
    func testReturnGenericClassValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericClassValueNative(value: sendValue)({ value, unit in
            XCTAssertEqual(value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 1)
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
        wait(for: [valueExpectation], timeout: 1)
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
        wait(for: [valueExpectation], timeout: 1)
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
        wait(for: [valueExpectation], timeout: 1)
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
        wait(for: [valueExpectation], timeout: 1)
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
        wait(for: [valueExpectation], timeout: 1)
    }
}
