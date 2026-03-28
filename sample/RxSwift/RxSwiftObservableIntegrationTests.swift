//
//  RxSwiftObservableIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 09/07/2021.
//

import XCTest
import KMPNativeCoroutinesRxSwift
import NativeCoroutinesSampleShared
#if NATIVE_COROUTINES_SWIFT_EXPORT
import KotlinRuntimeSupport
import KotlinCoroutineSupport
#endif

class RxSwiftObservableIntegrationTests: XCTestCase {
    
    func testValuesReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100)))
        #else
        let observable = createObservable(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(sendValueCount)
        let completionExpectation = expectation(description: "Waiting for completion")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        var receivedValueCount = 0
        let disposable = observable.subscribe(onNext: { value in
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertEqual(Int(value), receivedValueCount, "Received incorrect value")
            #else
            XCTAssertEqual(value.intValue, receivedValueCount, "Received incorrect value")
            #endif
            receivedValueCount += 1
            valuesExpectation.fulfill()
        }, onError: { _ in
            XCTFail("Observable should complete without an error")
        }, onCompleted: {
            completionExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation, disposedExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNilValueReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let nullValueIndex = randomInt(min: 0, max: sendValueCount - 1)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100)))
        #else
        let observable = createObservable(for: integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(sendValueCount)
        let completionExpectation = expectation(description: "Waiting for completion")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        var receivedValueCount = 0
        let disposable = observable.subscribe(onNext: { value in
            if receivedValueCount == nullValueIndex {
                XCTAssertNil(value, "Value should be nil")
            } else {
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                XCTAssertEqual(value.flatMap(Int.init), receivedValueCount, "Received incorrect value")
                #else
                XCTAssertEqual(value?.intValue, receivedValueCount, "Received incorrect value")
                #endif
            }
            receivedValueCount += 1
            valuesExpectation.fulfill()
        }, onError: { _ in
            XCTFail("Publisher should complete without an error")
        }, onCompleted: {
            completionExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation, disposedExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testExceptionReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let exceptionIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100)))
        #else
        let observable = createObservable(for: integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(exceptionIndex)
        let errorExpectation = expectation(description: "Waiting for error")
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = observable.subscribe(onNext: { _ in
            valuesExpectation.fulfill()
        }, onError: { error in
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertTrue(error is KotlinError, "Error isn't a KotlinError")
            let error = error as! KotlinError
            XCTAssertEqual(error.description, sendMessage, "Error has incorrect description")
            // TODO: Get actual Kotlin Exception
            #else
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
            #endif
            errorExpectation.fulfill()
        }, onCompleted: {
            completionExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, errorExpectation, completionExpectation, disposedExpectation], timeout: 4)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testErrorReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let errorIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100)))
        #else
        let observable = createObservable(for: integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(errorIndex)
        let errorExpectation = expectation(description: "Waiting for error")
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = observable.subscribe(onNext: { _ in
            valuesExpectation.fulfill()
        }, onError: { error in
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertTrue(error is KotlinError, "Error isn't a KotlinError")
            let error = error as! KotlinError
            XCTAssertEqual(error.description, sendMessage, "Error has incorrect description")
            // TODO: Get actual Kotlin Error
            #else
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
            #endif
            errorExpectation.fulfill()
        }, onCompleted: {
            completionExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, errorExpectation, completionExpectation, disposedExpectation], timeout: 4)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNotOnMainThread() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getFlow(count: 1, delay: 1000)))
        #else
        let observable = createObservable(for: integrationTests.getFlow(count: 1, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        XCTAssertTrue(Thread.isMainThread, "Test should run on the main thread")
        let disposable = observable.subscribe(onNext: { _ in
            XCTAssertFalse(Thread.isMainThread, "Value shouldn't be received on the main thread")
            valueExpectation.fulfill()
        }, onCompleted: {
            XCTAssertFalse(Thread.isMainThread, "Completion shouldn't be received on the main thread")
            completionExpectation.fulfill()
        }, onDisposed: {
            XCTAssertFalse(Thread.isMainThread, "Dispose shouldn't be received on the main thread")
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        wait(for: [valueExpectation, completionExpectation, disposedExpectation], timeout: 3)
    }
    
    func testCancellation() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 3, delay: 1000) {
            callbackExpectation.fulfill()
        }))
        #else
        let observable = createObservable(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 3, delay: 1000) {
            callbackExpectation.fulfill()
        })
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = 2
        let errorExpectation = expectation(description: "Waiting for error")
        errorExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = observable.subscribe(onNext: { _ in
            valuesExpectation.fulfill()
        }, onError: { _ in
            errorExpectation.fulfill()
        }, onCompleted: {
            completionExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation], timeout: 4)
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        disposable.dispose()
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        wait(for: [callbackExpectation, errorExpectation, completionExpectation, disposedExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testUnitValues() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: asyncSequence(for: integrationTests.getUnitFlow(count: 2, delay: 100)))
        #else
        let observable = createObservable(for: integrationTests.getUnitFlow(count: 2, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = 2
        let completionExpectation = expectation(description: "Waiting for completion")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        var receivedValueCount = 0
        let disposable = observable.subscribe(onNext: { _ in
            receivedValueCount += 1
            valuesExpectation.fulfill()
        }, onError: { _ in
            XCTFail("Observable should complete without an error")
        }, onCompleted: {
            completionExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation, disposedExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
}
