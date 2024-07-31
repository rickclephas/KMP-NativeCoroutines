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
        _ = integrationTests.returnGenericClassValue(value: sendValue)({ value, unit in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnDefaultValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = randomInt()
        _ = integrationTests.returnDefaultValue(value: sendValue)({ value, unit in
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericValue(value: sendValue)({ value, unit in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnConstrainedGenericValue() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = integrationTests.returnAppendable(value: randomString())
        _ = integrationTests.returnConstrainedGenericValue(value: sendValue)({ value, unit in
            XCTAssertIdentical(value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericValues() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for values")
        let sendValues = [NSNumber(value: randomInt()), NSNumber(value: randomInt())]
        _ = integrationTests.returnGenericValues(values: sendValues)({ values, unit in
            XCTAssertEqual(values as! [NSNumber], sendValues, "Received incorrect values")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericVarargValues() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for values")
        let sendValues = KotlinArray<AnyObject>(size: 2) { _ in
            NSNumber(value: self.randomInt())
        }
        _ = integrationTests.returnGenericVarargValues(values: sendValues)({ values, unit in
            XCTAssertEqual(values.size, sendValues.size, "Received incorrect number of value")
            for i in 0..<values.size {
                XCTAssertEqual(values.get(index: i) as! NSNumber, sendValues.get(index: i) as! NSNumber, "Received incorrect value at index \(i)")
            }
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericValueFromExtension() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericValueFromExtension([], value: sendValue)({ value, unit in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testReturnGenericFlow() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = NSNumber(value: randomInt())
        _ = integrationTests.returnGenericFlow(value: sendValue)({ value, next, _ in
            XCTAssertEqual(value as! NSNumber, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return next()
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
}
