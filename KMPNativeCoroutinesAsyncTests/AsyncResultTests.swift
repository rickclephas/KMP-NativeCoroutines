//
//  AsyncResultTests.swift
//  KMPNativeCoroutinesAsyncTests
//
//  Created by Rick Clephas on 28/06/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesAsync

class AsyncResultTests: XCTestCase {
    
    private class TestValue { }

    func testCancellableInvoked() async {
        var cancelCount = 0
        let nativeSuspend: NativeSuspend<String, Error> = { returnType, _, _, cancelCallback in
            guard returnType == nil else { return { nil } }
            return {
                cancelCount += 1
                _ = cancelCallback(NSError(domain: "Ignored", code: 0), ())
                return nil
            }
        }
        let handle = Task {
            await asyncResult(for: nativeSuspend)
        }
        XCTAssertEqual(cancelCount, 0, "Cancellable shouldn't be invoked yet")
        handle.cancel()
        let handleResult = await handle.result
        XCTAssertEqual(cancelCount, 1, "Cancellable should be invoked once")
        guard case let .success(result) = handleResult else {
            XCTFail("Task should complete without an error")
            return
        }
        guard case let .failure(error) = result else {
            XCTFail("Function should fail with an error")
            return
        }
        XCTAssertTrue(error is CancellationError, "Error should be a CancellationError")
    }
    
    func testCompletionWithValue() async {
        let value = TestValue()
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, resultCallback, _, _ in
            guard returnType == nil else { return { nil } }
            _ = resultCallback(value, ())
            return { nil }
        }
        let result = await asyncResult(for: nativeSuspend)
        guard case let .success(receivedValue) = result else {
            XCTFail("Function should return without an error")
            return
        }
        XCTAssertIdentical(receivedValue, value, "Received incorrect value")
    }
    
    func testCompletionWithError() async {
        let sendError = NSError(domain: "Test", code: 0)
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, _, errorCallback, _ in
            guard returnType == nil else { return { nil } }
            _ = errorCallback(sendError, ())
            return { nil }
        }
        let result = await asyncResult(for: nativeSuspend)
        guard case let .failure(error) = result else {
            XCTFail("Function should throw an error")
            return
        }
        XCTAssertEqual(error as NSError, sendError, "Received incorrect error")
    }
}
