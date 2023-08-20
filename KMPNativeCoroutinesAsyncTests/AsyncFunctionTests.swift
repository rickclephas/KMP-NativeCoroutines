//
//  AsyncFunctionTests.swift
//  KMPNativeCoroutinesAsyncTests
//
//  Created by Rick Clephas on 13/06/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesAsync

class AsyncFunctionTests: XCTestCase {
    
    private class TestValue { }

    func testCancellableInvoked() async {
        var cancelCount = 0
        let nativeSuspend: NativeSuspend<String, Error, Void> = { _, _, cancelCallback in
            return {
                cancelCount += 1
                cancelCallback(NSError(domain: "Ignored", code: 0), ())
            }
        }
        let handle = Task {
            try await asyncFunction(for: nativeSuspend)
        }
        XCTAssertEqual(cancelCount, 0, "Cancellable shouldn't be invoked yet")
        handle.cancel()
        let result = await handle.result
        XCTAssertEqual(cancelCount, 1, "Cancellable should be invoked once")
        guard case let .failure(error) = result else {
            XCTFail("Function should fail with an error")
            return
        }
        XCTAssertTrue(error is CancellationError, "Error should be a CancellationError")
    }
    
    func testCompletionWithValue() async {
        let value = TestValue()
        let nativeSuspend: NativeSuspend<TestValue, NSError, Void> = { resultCallback, _, _ in
            resultCallback(value, ())
            return { }
        }
        do {
            let receivedValue = try await asyncFunction(for: nativeSuspend)
            XCTAssertIdentical(receivedValue, value, "Received incorrect value")
        } catch {
            XCTFail("Function shouldn't throw an error")
        }
    }
    
    func testCompletionWithError() async {
        let sendError = NSError(domain: "Test", code: 0)
        let nativeSuspend: NativeSuspend<TestValue, NSError, Void> = { _, errorCallback, _ in
            errorCallback(sendError, ())
            return { }
        }
        do {
            _ = try await asyncFunction(for: nativeSuspend)
            XCTFail("Function should throw an error")
        } catch {
            XCTAssertEqual(error as NSError, sendError, "Received incorrect error")
        }
    }
}
