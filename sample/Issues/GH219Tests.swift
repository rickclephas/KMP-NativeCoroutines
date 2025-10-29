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
        let gh219 = GH219Kt.createGH219()
        XCTAssertEqual(gh219.state, "GH219")
    }
}
