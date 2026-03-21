//
//  CombineFutureIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest
import KMPNativeCoroutinesCombine
import NativeCoroutinesSampleShared
#if NATIVE_COROUTINES_SWIFT_EXPORT
import KotlinRuntimeSupport
#endif

class CombineFutureIntegrationTests: XCTestCase {
    
    func testValueReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendValue = randomInt()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.returnValue(value: sendValue, delay: 1000) })
        #else
        let future = createFuture(for: integrationTests.returnValue(value: sendValue, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Future should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { value in
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertEqual(value, sendValue, "Received incorrect value")
            #else
            XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
            #endif
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNilValueReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.returnNull(delay: 1000) })
        #else
        let future = createFuture(for: integrationTests.returnNull(delay: 1000))
        #endif
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
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testExceptionReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.throwException(message: sendMessage, delay: 1000) })
        #else
        let future = createFuture(for: integrationTests.throwException(message: sendMessage, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for no value")
        valueExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case let .failure(error) = completion {
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
            } else {
                XCTFail("Future should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testErrorReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.throwError(message: sendMessage, delay: 1000) })
        #else
        let future = createFuture(for: integrationTests.throwError(message: sendMessage, delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for no value")
        valueExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case let .failure(error) = completion {
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
            } else {
                XCTFail("Future should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNotOnMainThread() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.returnValue(value: 1, delay: 1000) })
        #else
        let future = createFuture(for: integrationTests.returnValue(value: 1, delay: 1000))
        #endif
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
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
    }
    
    func testCancellation() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.returnFromCallback(delay: 3000) {
            callbackExpectation.fulfill()
            return 1
        }})
        #else
        let future = createFuture(for: integrationTests.returnFromCallback(delay: 3000) {
            callbackExpectation.fulfill()
            return KotlinInt(int: 1)
        })
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        valueExpectation.isInverted = true
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let cancellable = future.sink { _ in
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valueExpectation.fulfill()
        }
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        delay(1)
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        cancellable.cancel()
        #if !NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        #endif
        wait(for: [callbackExpectation, valueExpectation, completionExpectation], timeout: 3)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testValuesReceived() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let publisher = createPublisher(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
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
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    #endif
    
    func testUnitReturnType() {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let future = createFuture(for: { try await integrationTests.returnUnit(delay: 1000) })
        #else
        let future = createFuture(for: integrationTests.returnUnit(delay: 1000))
        #endif
        let valueExpectation = expectation(description: "Waiting for value")
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = future.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Future should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: {
            valueExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
}
