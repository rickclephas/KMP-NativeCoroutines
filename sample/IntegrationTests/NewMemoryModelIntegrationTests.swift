//
//  NewMemoryModelIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 06/11/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import NativeCoroutinesSampleShared

class NewMemoryModelIntegrationTests: XCTestCase {
    
    private typealias IntegrationTests = NativeCoroutinesSampleShared.NewMemoryModelIntegrationTests
    
    func testReturnMutableData() {
        let integrationTests = IntegrationTests()
        let valueExpectation = expectation(description: "Waiting for value")
        _ = integrationTests.generateRandomMutableData()({ value, unit in
            let dataFromBackground = value.dataFromBackground
            let dataFromMain = value.dataFromMain
            XCTAssertNotNil(dataFromBackground, "Data from background should be set")
            XCTAssertNotNil(dataFromMain, "Data from main should be set")
            XCTAssertNotEqual(dataFromBackground, dataFromMain, "Data from background and main should be different")
            value.dataFromBackground = dataFromMain
            value.dataFromMain = dataFromBackground
            XCTAssertEqual(value.dataFromBackground, dataFromMain, "Data from background should now be data from main")
            XCTAssertEqual(value.dataFromMain, dataFromBackground, "Data from main should now be data from background")
            valueExpectation.fulfill()
            return unit
        }, { _, unit in unit }, { _, unit in unit })
        wait(for: [valueExpectation], timeout: 1)
    }
}
