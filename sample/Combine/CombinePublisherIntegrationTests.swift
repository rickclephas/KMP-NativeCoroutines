//
//  CombinePublisherIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest
import KMPNativeCoroutinesCombine
import NativeCoroutinesSampleShared

class CombinePublisherIntegrationTests: XCTestCase {
    
    func testValuesReceived() {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let publisher = createPublisher(for: integrationTests.getFlowNative(count: sendValueCount, delay: 100))
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(sendValueCount)
        let completionExpectation = expectation(description: "Waiting for completion")
        var receivedValueCount = 0
        let cancellable = publisher.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Publisher should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { value in
            XCTAssertEqual(value.intValue, receivedValueCount, "Received incorrect value")
            receivedValueCount += 1
            valuesExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNilValueReceived() {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let nullValueIndex = randomInt(min: 0, max: sendValueCount - 1)
        let publisher = createPublisher(for: integrationTests.getFlowWithNullNative(count: sendValueCount, nullIndex: nullValueIndex, delay: 100))
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(sendValueCount)
        let completionExpectation = expectation(description: "Waiting for completion")
        var receivedValueCount = 0
        let cancellable = publisher.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Publisher should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { value in
            if receivedValueCount == nullValueIndex {
                XCTAssertNil(value, "Value should be nil")
            } else {
                XCTAssertEqual(value?.intValue, receivedValueCount, "Received incorrect value")
            }
            receivedValueCount += 1
            valuesExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testExceptionReceived() {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let exceptionIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        let publisher = createPublisher(for: integrationTests.getFlowWithExceptionNative(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100))
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(exceptionIndex)
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = publisher.sink { completion in
            if case let .failure(error) = completion {
                let error = error as NSError
                XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
                let exception = error.userInfo["KotlinException"]
                XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
            } else {
                XCTFail("Publisher should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testErrorReceived() {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let errorIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        let publisher = createPublisher(for: integrationTests.getFlowWithErrorNative(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100))
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(errorIndex)
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = publisher.sink { completion in
            if case let .failure(error) = completion {
                let error = error as NSError
                XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
                let exception = error.userInfo["KotlinException"]
                XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
            } else {
                XCTFail("Publisher should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNotOnMainThread() {
        let integrationTests = FlowIntegrationTests()
        let publisher = createPublisher(for: integrationTests.getFlowNative(count: 1, delay: 1000))
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        XCTAssertTrue(Thread.isMainThread, "Test should run on the main thread")
        let cancellable = publisher.sink { _ in
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
        let integrationTests = FlowIntegrationTests()
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        let publisher = createPublisher(for: integrationTests.getFlowWithCallbackNative(count: 5, callbackIndex: 2, delay: 1000) {
            callbackExpectation.fulfill()
        })
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = 2
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let cancellable = publisher.sink { _ in
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation], timeout: 4)
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        cancellable.cancel()
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        wait(for: [callbackExpectation, completionExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
}
