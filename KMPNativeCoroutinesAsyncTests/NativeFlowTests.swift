//
//  NativeFlowTests.swift
//  KMPNativeCoroutinesAsyncTests
//
//  Created by Rick Clephas on 25/11/2023.
//

import XCTest
import KMPNativeCoroutinesCore
@testable import KMPNativeCoroutinesAsync

class NativeFlowTests: XCTestCase {
    
    private class TestValue { }
    
    func testAsyncSequenceReturnTypeReturnsAsyncSequence() {
        let nativeFlow = AsyncStream<TestValue> { _ in }.asNativeFlow()
        let cancellable = nativeFlow(RETURN_TYPE_SWIFT_ASYNC_SEQUENCE, EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssert(cancellable() is AnyAsyncSequence<TestValue>, "Should return AnyAsyncSequence")
    }
    
    func testUnknownReturnTypeReturnsNil() {
        let nativeFlow = AsyncStream<TestValue> { _ in }.asNativeFlow()
        let cancellable = nativeFlow("unknown", EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssertNil(cancellable())
    }
    
    func testCompletionCallbackIsInvoked() {
        let nativeFlow = AsyncStream<TestValue> { continuation in
            continuation.yield(TestValue())
            continuation.finish()
        }.asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeFlow(nil, { _, next, _ in next() }, { error, unit in
            XCTAssertNil(error)
            completionExpectation.fulfill()
            return unit
        }, { _, unit in
            cancellationExpectation.fulfill()
            return unit
        })
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testErrorsAreReceived() {
        let error = NSError(domain: "Test", code: 0)
        let nativeFlow = AsyncThrowingStream<TestValue, Error> { continuation in
            continuation.finish(throwing: error)
        }.asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeFlow(nil, { _, _, _ in }, { receivedError, unit in
            XCTAssertEqual(receivedError as NSError?, error)
            completionExpectation.fulfill()
            return unit
        }, { _, unit in
            cancellationExpectation.fulfill()
            return unit
        })
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testValuesAreReceived() {
        let values = [TestValue(), TestValue(), TestValue(), TestValue()]
        let nativeFlow = AsyncStream<TestValue> { continuation in
            for value in values {
                continuation.yield(value)
            }
            continuation.finish()
        }.asNativeFlow()
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = values.count
        var receivedValueCount = 0
        _ = nativeFlow(nil, { value, next, _ in
            XCTAssertIdentical(value, values[receivedValueCount], "Received incorrect value")
            receivedValueCount += 1
            valuesExpectation.fulfill()
            return next()
        }, { _, _ in }, { _, _ in })
        wait(for: [valuesExpectation], timeout: 4)
    }
    
    func testCollectionIsCancelled() {
        let nativeFlow = AsyncStream<TestValue> { _ in }.asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for no completion")
        completionExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for cancellation")
        let cancel = nativeFlow(nil, { _, _, _ in }, { _, unit in
            completionExpectation.fulfill()
            return unit
        }, { error, unit in
            XCTAssert(error is CancellationError, "Error should be a CancellationError")
            cancellationExpectation.fulfill()
            return unit
        })
        _ = cancel()
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
}
