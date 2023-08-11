//
//  PublisherTests.swift
//  KMPNativeCoroutinesCombineTests
//
//  Created by Rick Clephas on 12/06/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesCombine

class PublisherTests: XCTestCase {
    
    private class TestValue { }

    func testCancellableInvoked() {
        var cancelCount = 0
        let nativeFlow: NativeFlow<TestValue, NSError, Void> = { _, _, _ in
            return { cancelCount += 1 }
        }
        let cancellable = createPublisher(for: nativeFlow)
            .sink { _ in } receiveValue: { _ in }
        XCTAssertEqual(cancelCount, 0, "Cancellable shouldn't be invoked yet")
        cancellable.cancel()
        XCTAssertEqual(cancelCount, 1, "Cancellable should be invoked once")
    }
    
    func testCompletionWithCorrectValues() {
        let values = [TestValue(), TestValue(), TestValue(), TestValue(), TestValue()]
        let nativeFlow: NativeFlow<TestValue, NSError, Void> = { itemCallback, completionCallback, _ in
            DispatchQueue.main.async {
                for value in values {
                    itemCallback(value, {}, ())
                }
                completionCallback(nil, ())
            }
            return { }
        }
        let completionExpectation = expectation(description: "Waiting for completion")
        var valueCount = 0
        let cancellable = createPublisher(for: nativeFlow)
            .sink { completion in
                guard case .finished = completion else {
                    XCTFail("Publisher should complete without error")
                    return
                }
                completionExpectation.fulfill()
            } receiveValue: { receivedValue in
                XCTAssertIdentical(receivedValue, values[valueCount], "Received incorrect value")
                valueCount += 1
            }
        _ = cancellable // This is just to remove the unused variable warning
        wait(for: [completionExpectation], timeout: 4)
        XCTAssertEqual(valueCount, values.count, "Value closure should be called for every value")
    }
    
    func testCompletionWithError() {
        let error = NSError(domain: "Test", code: 0)
        let nativeFlow: NativeFlow<TestValue, NSError, Void> = { _, completionCallback, _ in
            completionCallback(error, ())
            return { }
        }
        var completionCount = 0
        var valueCount = 0
        let cancellable = createPublisher(for: nativeFlow)
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
}

