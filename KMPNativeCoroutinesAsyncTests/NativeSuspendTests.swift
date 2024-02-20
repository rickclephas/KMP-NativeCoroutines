//
//  NativeSuspendTests.swift
//  KMPNativeCoroutinesAsyncTests
//
//  Created by Rick Clephas on 26/11/2023.
//

import XCTest
import KMPNativeCoroutinesCore
@testable import KMPNativeCoroutinesAsync

class NativeSuspendTests: XCTestCase {
    
    private class TestValue { }
    
    func testAsyncReturnTypeReturnsOperation() {
        let value = TestValue()
        let operation: (@Sendable () async throws -> TestValue) = { value }
        let nativeSuspend = nativeSuspend(operation: operation)
        let cancellable = nativeSuspend(RETURN_TYPE_SWIFT_ASYNC, EmptyNativeCallback, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssert(cancellable() is (@Sendable () async throws -> TestValue))
    }
    
    func testUnknownReturnTypeReturnsNil() {
        let nativeSuspend = nativeSuspend { }
        let cancellable = nativeSuspend("unknown", EmptyNativeCallback, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssertNil(cancellable())
    }
    
    func testCorrectResultIsReceived() {
        let value = TestValue()
        let nativeSuspend = nativeSuspend { value }
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
        let nativeSuspend = nativeSuspend { throw error }
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
    
    func testFunctionIsCancelled() {
        let runExpectation = expectation(description: "Waiting for function to run")
        let nativeSuspend = nativeSuspend {
            runExpectation.fulfill()
            try await Task.sleep(nanoseconds: 10_000_000_000)
            return TestValue()
        }
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
        wait(for: [runExpectation], timeout: 2)
        _ = cancel()
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
}
