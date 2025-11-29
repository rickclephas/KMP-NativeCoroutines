//
//  GH206.swift
//  KMPNativeCoroutinesSample
//
//  Created by Rick Clephas on 04/03/2025.
//

import XCTest
import KMPNativeCoroutinesCore
import NativeCoroutinesSampleShared

class GH206Tests: XCTestCase {
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testReturnFlowValue() {
        let valueExpectation = expectation(description: "Waiting for value")
        _ = GH206().property({ value, next, _ in
            XCTAssertEqual(value, "GH206", "Received incorrect value")
            valueExpectation.fulfill()
            return next()
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 2)
    }
    #endif
}
