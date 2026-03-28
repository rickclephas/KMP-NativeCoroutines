//
//  RxSwiftSingleIntegrationTests.swift
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

class RxSwiftSingleIntegrationTests: XCTestCase {
    
    func testValueReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendValue = randomInt()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnValue(value: sendValue, delay: 1000) })
        #else
        let single = createSingle(for: integrationTests.returnValue(value: sendValue, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = single.subscribe(onSuccess: { value in
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertEqual(value, sendValue, "Received incorrect value")
            #else
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            #endif
            valueExpectation.fulfill()
        }, onFailure: { _ in
            XCTFail("Single shouldn't fail")
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNilValueReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnNull(delay: 1000) })
        #else
        let single = createSingle(for: integrationTests.returnNull(delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = single.subscribe(onSuccess: { value in
            XCTAssertNil(value, "Value should be nil")
            valueExpectation.fulfill()
        }, onFailure: { _ in
            XCTFail("Single shouldn't fail")
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testExceptionReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.throwException(message: sendMessage, delay: 1000) })
        #else
        let single = createSingle(for: integrationTests.throwException(message: sendMessage, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for no value")
        valueExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for error")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = single.subscribe(onSuccess: { _ in
            valueExpectation.fulfill()
        }, onFailure: { error in
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
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, errorExpectation, disposedExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testErrorReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.throwError(message: sendMessage, delay: 1000) })
        #else
        let single = createSingle(for: integrationTests.throwError(message: sendMessage, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for no value")
        valueExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for error")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = single.subscribe(onSuccess: { _ in
            valueExpectation.fulfill()
        }, onFailure: { error in
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
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, errorExpectation, disposedExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNotOnMainThread() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnValue(value: 1, delay: 1000) })
        #else
        let single = createSingle(for: integrationTests.returnValue(value: 1, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        XCTAssertTrue(Thread.isMainThread, "Test should run on the main thread")
        let disposable = single.subscribe(onSuccess: { _ in
            XCTAssertFalse(Thread.isMainThread, "Value shouldn't be received on the main thread")
            valueExpectation.fulfill()
        }, onDisposed: {
            XCTAssertFalse(Thread.isMainThread, "Disposed shouldn't be received on the main thread")
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
    }
    
    func testCancellation() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnFromCallback(delay: 3000) {
            callbackExpectation.fulfill()
            return 1
        }})
        #else
        let single = createSingle(for: integrationTests.returnFromCallback(delay: 3000) {
            callbackExpectation.fulfill()
            return KotlinInt(int: 1)
        })
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        valueExpectation.isInverted = true
        let errorExpectation = expectation(description: "Waiting for error")
        errorExpectation.isInverted = true
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = single.subscribe(onSuccess: { _ in
            valueExpectation.fulfill()
        }, onFailure: { _ in
            errorExpectation.fulfill()
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        delay(1)
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        disposable.dispose()
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        #endif
        wait(for: [callbackExpectation, valueExpectation, errorExpectation, disposedExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testValuesReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let observable = createObservable(for: { try await asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100)) })
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
        delay(0.2) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation, disposedExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testUnitReturnType() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnUnit(delay: 1000) })
        #else
        let single = createSingle(for: integrationTests.returnUnit(delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        let disposable = single.subscribe(onSuccess: {
            valueExpectation.fulfill()
        }, onFailure: { _ in
            XCTFail("Single shouldn't fail")
        }, onDisposed: {
            disposedExpectation.fulfill()
        })
        _ = disposable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
}
