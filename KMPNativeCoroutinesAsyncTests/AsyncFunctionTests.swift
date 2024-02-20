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
        let nativeSuspend: NativeSuspend<String, Error> = { returnType, _, _, cancelCallback in
            guard returnType == nil else { return { nil } }
            return {
                cancelCount += 1
                _ = cancelCallback(NSError(domain: "Ignored", code: 0), ())
                return nil
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
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, resultCallback, _, _ in
            guard returnType == nil else { return { nil } }
            _ = resultCallback(value, ())
            return { nil }
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
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, _, errorCallback, _ in
            guard returnType == nil else { return { nil } }
            _ = errorCallback(sendError, ())
            return { nil }
        }
        do {
            _ = try await asyncFunction(for: nativeSuspend)
            XCTFail("Function should throw an error")
        } catch {
            XCTAssertEqual(error as NSError, sendError, "Received incorrect error")
        }
    }
    
    func testAsyncFunctionIsOriginal() async {
        let nativeSuspend = nativeSuspend { TestValue() }
        do {
            _ = try await asyncFunction { returnType, onResult, onError, onCancelled in
                if let returnType {
                    return nativeSuspend(returnType, onResult, onError, onCancelled)
                }
                XCTFail("Async function should be returned")
                return { nil }
            }
        } catch {
            XCTFail("Function shouldn't throw an error")
        }
    }
}
