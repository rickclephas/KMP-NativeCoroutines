//
//  NativeFlowTests.swift
//  KMPNativeCoroutinesCombineTests
//
//  Created by Rick Clephas on 26/11/2023.
//

import XCTest
import Combine
import KMPNativeCoroutinesCore
@testable import KMPNativeCoroutinesCombine

class NativeFlowTests: XCTestCase {
    
    private class TestValue { }
    
    func testPublisherReturnTypeReturnsPublisher() {
        let nativeFlow = Just(TestValue()).asNativeFlow()
        let cancellable = nativeFlow(RETURN_TYPE_COMBINE_PUBLISHER, EmptyNativeCallback2, EmptyNativeCallback1, EmptyNativeCallback1)
        XCTAssert(cancellable() is AnyPublisher<TestValue, Error>, "Should return AnyPublisher")
    }
    
    func testUnknownReturnTypeReturnsNil() {
        let nativeFlow = Just(TestValue()).asNativeFlow()
        let cancellable = nativeFlow("unknown", EmptyNativeCallback2, EmptyNativeCallback1, EmptyNativeCallback1)
        XCTAssertNil(cancellable())
    }
    
    func testCompletionCallbackIsInvoked() {
        let nativeFlow = Just(TestValue()).asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeFlow(nil, { _, next in next() }, { error in
            XCTAssertNil(error)
            completionExpectation.fulfill()
            return nil
        }, { _ in
            cancellationExpectation.fulfill()
            return nil
        })
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testErrorsAreReceived() {
        let error = NSError(domain: "Test", code: 0)
        let nativeFlow = Fail<TestValue, NSError>(error: error).asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeFlow(nil, EmptyNativeCallback2, { receivedError in
            XCTAssertEqual(receivedError as NSError?, error)
            completionExpectation.fulfill()
            return nil
        }, { _ in
            cancellationExpectation.fulfill()
            return nil
        })
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testValuesAreReceived() {
        let values = [TestValue(), TestValue(), TestValue(), TestValue()]
        let nativeFlow = Publishers.Sequence<[NativeFlowTests.TestValue], Error>(sequence: values).asNativeFlow()
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = values.count
        var receivedValueCount = 0
        _ = nativeFlow(nil, { value, next in
            XCTAssertIdentical(value, values[receivedValueCount], "Received incorrect value")
            receivedValueCount += 1
            valuesExpectation.fulfill()
            return next()
        }, EmptyNativeCallback1, EmptyNativeCallback1)
        wait(for: [valuesExpectation], timeout: 4)
    }
    
    func testPublisherIsCancelled() {
        let nativeFlow = Just(TestValue()).delay(for: .seconds(5), scheduler: RunLoop.main).asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for no completion")
        completionExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for cancellation")
        let cancel = nativeFlow(nil, EmptyNativeCallback2, { _ in
            completionExpectation.fulfill()
            return nil
        }, { error in
            XCTAssert(error is CancellationError, "Error should be a CancellationError")
            cancellationExpectation.fulfill()
            return nil
        })
        _ = cancel()
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
}
