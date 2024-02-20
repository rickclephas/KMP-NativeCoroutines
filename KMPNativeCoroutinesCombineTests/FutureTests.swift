//
//  FutureTests.swift
//  KMPNativeCoroutinesCombineTests
//
//  Created by Rick Clephas on 12/06/2021.
//

import XCTest
import Combine
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesCombine

class FutureTests: XCTestCase {
    
    private class TestValue { }

    func testCancellableInvoked() {
        var cancelCount = 0
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, _, _, _ in
            guard returnType == nil else { return { nil } }
            return {
                cancelCount += 1
                return nil
            }
        }
        let cancellable = createFuture(for: nativeSuspend)
            .sink { _ in } receiveValue: { _ in }
        XCTAssertEqual(cancelCount, 0, "Cancellable shouldn't be invoked yet")
        cancellable.cancel()
        XCTAssertEqual(cancelCount, 1, "Cancellable should be invoked once")
    }
    
    func testCompletionWithValue() {
        let value = TestValue()
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, resultCallback, _, _ in
            guard returnType == nil else { return { nil } }
            _ = resultCallback(value, ())
            return { nil }
        }
        var completionCount = 0
        var valueCount = 0
        let cancellable = createFuture(for: nativeSuspend)
            .sink { completion in
                guard case .finished = completion else {
                    XCTFail("Publisher should complete without error")
                    return
                }
                completionCount += 1
            } receiveValue: { receivedValue in
                XCTAssertIdentical(receivedValue, value, "Received incorrect value")
                valueCount += 1
            }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(completionCount, 1, "Completion closure should be called once")
        XCTAssertEqual(valueCount, 1, "Value closure should be called once")
    }
    
    func testCompletionWithError() {
        let error = NSError(domain: "Test", code: 0)
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, _, errorCallback, _ in
            guard returnType == nil else { return { nil } }
            _ = errorCallback(error, ())
            return { nil }
        }
        var completionCount = 0
        var valueCount = 0
        let cancellable = createFuture(for: nativeSuspend)
            .sink { completion in
                guard case let .failure(receivedError) = completion else {
                    XCTFail("Publisher should complete with an error")
                    return
                }
                XCTAssertIdentical(receivedError, error, "Received incorrect error")
                completionCount += 1
            } receiveValue: { _ in
                valueCount += 1
            }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(completionCount, 1, "Completion closure should be called once")
        XCTAssertEqual(valueCount, 0, "Value closure shouldn't be called")
    }
    
    func testFutureIsOriginal() {
        let nativeSuspend = Just(TestValue()).asNativeSuspend()
        _ = createFuture { returnType, onResult, onError, onCancelled in
            if let returnType {
                return nativeSuspend(returnType, onResult, onError, onCancelled)
            }
            XCTFail("Future should be returned")
            return { nil }
        }
    }
}
