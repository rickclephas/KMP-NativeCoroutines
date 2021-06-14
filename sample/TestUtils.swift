//
//  TestUtils.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest

extension XCTestCase {
    
    func randomInt(min: Int32 = .min, max: Int32 = .max) -> Int32 {
        Int32.random(in: min...max)
    }
    
    func randomString() -> String {
        (1...10).map { _ in String(Unicode.Scalar(UInt8.random(in: 65...90))) }.joined()
    }
    
    func delay(_ timeInterval: TimeInterval) {
        let delayExpectation = XCTestExpectation()
        delayExpectation.isInverted = true
        wait(for: [delayExpectation], timeout: timeInterval)
    }
}
