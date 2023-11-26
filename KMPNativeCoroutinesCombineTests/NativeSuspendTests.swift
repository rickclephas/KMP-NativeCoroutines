//
//  NativeSuspendTests.swift
//  KMPNativeCoroutinesCombineTests
//
//  Created by Rick Clephas on 26/11/2023.
//

import XCTest
import Combine
import KMPNativeCoroutinesCore
@testable import KMPNativeCoroutinesCombine

class NativeSuspendTests: XCTestCase {
    
    private class TestValue { }
    
    func testFutureReturnTypeReturnsPublisher() {
        let nativeSuspend = Just(TestValue()).asNativeSuspend()
        let cancellable = nativeSuspend(RETURN_TYPE_COMBINE_FUTURE, EmptyNativeCallback, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssert(cancellable() is AnyPublisher<TestValue, Error>, "Should return AnyPublisher")
    }
    
    func testUnknownReturnTypeReturnsNil() {
        let nativeSuspend = Just(TestValue()).asNativeSuspend()
        let cancellable = nativeSuspend("unknown", EmptyNativeCallback, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssertNil(cancellable())
    }
    
    func testCorrectResultIsReceived() {
        let value = TestValue()
        let nativeSuspend = Just(value).asNativeSuspend()
        let resultExpectation = expectation(description: "Waiting for result")
        let errorExpectation = expectation(description: "Waiting for no error")
        errorExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeSuspend(nil, { receivedValue, unit in
            XCTAssertIdentical(receivedValue, value, "Received incorrect value")
            resultExpectation.fulfill()
            return unit
        }, { _, unit in
            errorExpectation.fulfill()
            return unit
        }, { _, unit in
            cancellationExpectation.fulfill()
            return unit
        })
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testErrorsAreReceived() {
        let error = NSError(domain: "Test", code: 0)
        let nativeSuspend = Fail<TestValue, NSError>(error: error).asNativeSuspend()
        let resultExpectation = expectation(description: "Waiting for no result")
        resultExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for error")
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeSuspend(nil, { _, unit in
            resultExpectation.fulfill()
            return unit
        }, { receivedError, unit in
            XCTAssertEqual(receivedError as NSError, error)
            errorExpectation.fulfill()
            return unit
        }, { _, unit in
            cancellationExpectation.fulfill()
            return unit
        })
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testFutureIsCancelled() {
        let nativeSuspend = Just(TestValue()).delay(for: .seconds(5), scheduler: RunLoop.main).asNativeSuspend()
        let resultExpectation = expectation(description: "Waiting for no result")
        resultExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for no error")
        errorExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for cancellation")
        let cancel = nativeSuspend(nil, { _, unit in
            resultExpectation.fulfill()
            return unit
        }, { _, unit in
            errorExpectation.fulfill()
            return unit
        }, { error, unit in
            XCTAssert(error is CancellationError, "Error should be a CancellationError")
            cancellationExpectation.fulfill()
            return unit
        })
        _ = cancel()
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
}
