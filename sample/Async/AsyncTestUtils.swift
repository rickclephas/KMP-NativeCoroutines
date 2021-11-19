//
//  AsyncTestUtils.swift
//  Sample
//
//  Created by Rick Clephas on 19/11/2021.
//

import XCTest
import NativeCoroutinesSampleShared

func assertJobCompleted(_ integrationTests: IntegrationTests) async {
    await withCheckedContinuation { (continuation: CheckedContinuation<Void, Never>) in
        // The job should complete soon after the stream finishes
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
            continuation.resume()
        }
    }
}
