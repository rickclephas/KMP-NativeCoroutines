//
//  NativeSuspendTests.swift
//  KMPNativeCoroutinesRxSwiftTests
//
//  Created by Rick Clephas on 26/11/2023.
//

import XCTest
import RxSwift
import KMPNativeCoroutinesCore
@testable import KMPNativeCoroutinesRxSwift

class NativeSuspendTests: XCTestCase {
    
    private class TestValue { }
    
    func testSingleReturnTypeReturnsPublisher() {
        let nativeSuspend = Single.just(TestValue()).asNativeSuspend()
        let cancellable = nativeSuspend(RETURN_TYPE_RXSWIFT_SINGLE, EmptyNativeCallback1, EmptyNativeCallback1, EmptyNativeCallback1)
        XCTAssert(cancellable() is Single<TestValue>, "Should return Single")
    }
    
    func testUnknownReturnTypeReturnsNil() {
        let nativeSuspend = Single.just(TestValue()).asNativeSuspend()
        let cancellable = nativeSuspend("unknown", EmptyNativeCallback1, EmptyNativeCallback1, EmptyNativeCallback1)
        XCTAssertNil(cancellable())
    }
    
    func testCorrectResultIsReceived() {
        let value = TestValue()
        let nativeSuspend = Single.just(value).asNativeSuspend()
        let resultExpectation = expectation(description: "Waiting for result")
        let errorExpectation = expectation(description: "Waiting for no error")
        errorExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeSuspend(nil, { receivedValue in
            XCTAssertIdentical(receivedValue, value, "Received incorrect value")
            resultExpectation.fulfill()
            return nil
        }, { _ in
            errorExpectation.fulfill()
            return nil
        }, { _ in
            cancellationExpectation.fulfill()
            return nil
        })
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testErrorsAreReceived() {
        let error = NSError(domain: "Test", code: 0)
        let nativeSuspend = Single<TestValue>.error(error).asNativeSuspend()
        let resultExpectation = expectation(description: "Waiting for no result")
        resultExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for error")
        let cancellationExpectation = expectation(description: "Waiting for no cancellation")
        cancellationExpectation.isInverted = true
        _ = nativeSuspend(nil, { _ in
            resultExpectation.fulfill()
            return nil
        }, { receivedError in
            XCTAssertEqual(receivedError as NSError, error)
            errorExpectation.fulfill()
            return nil
        }, { _ in
            cancellationExpectation.fulfill()
            return nil
        })
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
    
    func testSingleIsCancelled() {
        let nativeSuspend = Single.just(TestValue()).delay(.seconds(5), scheduler: MainScheduler.instance).asNativeSuspend()
        let resultExpectation = expectation(description: "Waiting for no result")
        resultExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for no error")
        errorExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for cancellation")
        let cancel = nativeSuspend(nil, { _ in
            resultExpectation.fulfill()
            return nil
        }, { _ in
            errorExpectation.fulfill()
            return nil
        }, { error in
            XCTAssert(error is DisposedError, "Error should be a DisposedError")
            cancellationExpectation.fulfill()
            return nil
        })
        _ = cancel()
        wait(for: [resultExpectation, errorExpectation, cancellationExpectation], timeout: 4)
    }
}
