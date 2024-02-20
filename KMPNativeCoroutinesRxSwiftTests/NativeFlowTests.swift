//
//  NativeFlowTests.swift
//  KMPNativeCoroutinesRxSwiftTests
//
//  Created by Rick Clephas on 26/11/2023.
//

import XCTest
import RxSwift
import KMPNativeCoroutinesCore
@testable import KMPNativeCoroutinesRxSwift

class NativeFlowTests: XCTestCase {
    
    private class TestValue { }
    
    func testObservableReturnTypeReturnsPublisher() {
        let nativeFlow = Observable.just(TestValue()).asNativeFlow()
        let cancellable = nativeFlow(RETURN_TYPE_RXSWIFT_OBSERVABLE, EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssert(cancellable() is Observable<TestValue>, "Should return Observable")
    }
    
    func testUnknownReturnTypeReturnsNil() {
        let nativeFlow = Observable.just(TestValue()).asNativeFlow()
        let cancellable = nativeFlow("unknown", EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)
        XCTAssertNil(cancellable())
    }
    
    func testCompletionCallbackIsInvoked() {
        let nativeFlow = Observable.just(TestValue()).asNativeFlow()
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
        let nativeFlow = Observable<TestValue>.error(error).asNativeFlow()
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
        let nativeFlow = Observable.from(values).asNativeFlow()
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
    
    func testObservableIsCancelled() {
        let nativeFlow = Observable.just(TestValue()).delay(.seconds(5), scheduler: MainScheduler.instance).asNativeFlow()
        let completionExpectation = expectation(description: "Waiting for no completion")
        completionExpectation.isInverted = true
        let cancellationExpectation = expectation(description: "Waiting for cancellation")
        let cancel = nativeFlow(nil, { _, _, _ in }, { _, unit in
            completionExpectation.fulfill()
            return unit
        }, { error, unit in
            XCTAssert(error is DisposedError, "Error should be a DisposedError")
            cancellationExpectation.fulfill()
            return unit
        })
        _ = cancel()
        wait(for: [completionExpectation, cancellationExpectation], timeout: 4)
    }
}
