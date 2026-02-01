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
#endif

class RxSwiftSingleIntegrationTests: XCTestCase {
    
    func testValueReceived() {
        let integrationTests = KotlinSuspendIntegrationTests()
        let sendValue = randomInt()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnValueNative(value: sendValue, delay: 1000) })
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
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        #endif
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNilValueReceived() {
        let integrationTests = KotlinSuspendIntegrationTests()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnNullNative(delay: 1000) })
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
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        #endif
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testExceptionReceived() {
        let integrationTests = KotlinSuspendIntegrationTests()
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.throwExceptionNative(message: sendMessage, delay: 1000) })
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
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        #endif
        wait(for: [valueExpectation, errorExpectation, disposedExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Error throwing isn't supported yet, see https://youtrack.jetbrains.com/issue/KT-83389
    func testErrorReceived() {
        let integrationTests = KotlinSuspendIntegrationTests()
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.throwErrorNative(message: sendMessage, delay: 1000) })
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
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        #endif
        wait(for: [valueExpectation, errorExpectation, disposedExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    #endif
    
    func testNotOnMainThread() {
        let integrationTests = KotlinSuspendIntegrationTests()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnValueNative(value: 1, delay: 1000) })
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
        let integrationTests = KotlinSuspendIntegrationTests()
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let single = createSingle(for: { try await integrationTests.returnFromCallbackNative(delay: 3000) {
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
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        #endif
        delay(1)
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        #endif
        disposable.dispose()
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        wait(for: [callbackExpectation, valueExpectation, errorExpectation, disposedExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testValuesReceived() {
        let integrationTests = SuspendIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let observable = createObservable(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(sendValueCount)
        let completionExpectation = expectation(description: "Waiting for completion")
        let disposedExpectation = expectation(description: "Waiting for dispose")
        var receivedValueCount = 0
        let disposable = observable.subscribe(onNext: { value in
            XCTAssertEqual(value.intValue, receivedValueCount, "Received incorrect value")
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
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation, disposedExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Suspend functions returning Unit aren't supported yet, see https://youtrack.jetbrains.com/issue/KT-81593
    func testUnitReturnType() {
        let integrationTests = SuspendIntegrationTests()
        let single = createSingle(for: integrationTests.returnUnit(delay: 1000))
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
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, disposedExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    #endif
}
