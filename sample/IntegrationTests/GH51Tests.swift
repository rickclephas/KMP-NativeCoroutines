//
//  GH51Tests.swift
//  Sample
//
//  Created by Rick Clephas on 26/03/2022.
//

import XCTest
import KMPNativeCoroutinesCore
import NativeCoroutinesSampleShared

class GH51Tests: XCTestCase {
    
    func testInterfaceCImplFoo() {
        let interfaceC = InterfaceCImpl()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = randomInt()
        _ = GH51NativeKt.foo(interfaceC, value: sendValue)({ value, next, _ in
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return next()
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testInterfaceCImplBar() {
        let interfaceC = InterfaceCImpl()
        let valueExpectation = expectation(description: "Waiting for value")
        _ = GH51NativeKt.bar(interfaceC)({ value, next, _ in
            XCTAssertEqual(value, 1, "Received incorrect value")
            valueExpectation.fulfill()
            return next()
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testClassCImplFoo() {
        let classC = ClassCImpl()
        let valueExpectation = expectation(description: "Waiting for value")
        let sendValue = randomInt()
        _ = classC.foo(value: sendValue)({ value, next, _ in
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
            return next()
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    
    func testClassCImplBar() {
        let classC = ClassCImpl()
        let valueExpectation = expectation(description: "Waiting for value")
        _ = classC.bar({ value, next, _ in
            XCTAssertEqual(value, 1, "Received incorrect value")
            valueExpectation.fulfill()
            return next()
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
}
