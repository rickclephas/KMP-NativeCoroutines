//
//  CombineFutureIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest
import KMPNativeCoroutinesCombine
import NativeCoroutinesSampleShared

class CombineFutureIntegrationTests: XCTestCase {
    
    func testValueReceived() {
        let integrationTests = SuspendIntegrationTests()
        let sendValue = randomInt()
        let future = createFuture(for: integrationTests.returnValueNative(value: sendValue, delay: 1000))
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Future should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { value in
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNilValueReceived() {
        let integrationTests = SuspendIntegrationTests()
        let future = createFuture(for: integrationTests.returnNullNative(delay: 1000))
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Future should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { value in
            XCTAssertNil(value, "Value should be nil")
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testExceptionReceived() {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        let future = createFuture(for: integrationTests.throwExceptionNative(message: sendMessage, delay: 1000))
        let valueExpectation = expectation(description: "Waiting for no value")
        valueExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case let .failure(error) = completion {
                let error = error as NSError
                XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
                let exception = error.userInfo["KotlinException"]
                XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
            } else {
                XCTFail("Future should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testErrorReceived() {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        let future = createFuture(for: integrationTests.throwErrorNative(message: sendMessage, delay: 1000))
        let valueExpectation = expectation(description: "Waiting for no value")
        valueExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case let .failure(error) = completion {
                let error = error as NSError
                XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
                let exception = error.userInfo["KotlinException"]
                XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
            } else {
                XCTFail("Future should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNotOnMainThread() {
        let integrationTests = SuspendIntegrationTests()
        let future = createFuture(for: integrationTests.returnValueNative(value: 1, delay: 1000))
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        XCTAssertTrue(Thread.isMainThread, "Test should run on the main thread")
        let cancellable = future.sink { _ in
            XCTAssertFalse(Thread.isMainThread, "Completion shouldn't be received on the main thread")
            completionExpectation.fulfill()
        } receiveValue: { _ in
            XCTAssertFalse(Thread.isMainThread, "Value shouldn't be received on the main thread")
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        wait(for: [valueExpectation, completionExpectation], timeout: 2)
    }
    
    func testCancellation() {
        let integrationTests = SuspendIntegrationTests()
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        let future = createFuture(for: integrationTests.returnFromCallbackNative(delay: 3000) {
            callbackExpectation.fulfill()
            return KotlinInt(int: 1)
        })
        let valueExpectation = expectation(description: "Waiting for value")
        valueExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let cancellable = future.sink { _ in
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valueExpectation.fulfill()
        }
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        delay(1)
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        cancellable.cancel()
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        wait(for: [callbackExpectation, valueExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
}
