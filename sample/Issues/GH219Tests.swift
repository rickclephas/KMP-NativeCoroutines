//
//  GH219Tests.swift
//  KMPNativeCoroutinesSample
//
//  Created by Rick Clephas on 04/06/2025.
//

import XCTest
import NativeCoroutinesSampleShared

class GH219Tests: XCTestCase {
    
    func testReturnStateFlowValue() {
        XCTAssertEqual(GH219Kt.createGH219().state, "GH219")
    }
}
