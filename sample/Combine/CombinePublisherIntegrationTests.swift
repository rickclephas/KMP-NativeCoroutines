//
//  CombinePublisherIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest
import KMPNativeCoroutinesCombine
import NativeCoroutinesSampleShared
#if NATIVE_COROUTINES_SWIFT_EXPORT
import KotlinRuntimeSupport
import KotlinCoroutineSupport
#endif

class CombinePublisherIntegrationTests: XCTestCase {
    
    func testValuesReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100)))
        #else
        let publisher = createPublisher(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        #endif
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
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertEqual(Int(value), receivedValueCount, "Received incorrect value")
            #else
            XCTAssertEqual(value.intValue, receivedValueCount, "Received incorrect value")
            #endif
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
    
    @available(iOS 15.0, macOS 12.0, tvOS 15.0, watchOS 8.0, *)
    func testValueBackPressure() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount: Int32 = 10
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100)))
        #else
        let publisher = createPublisher(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        #endif
        do {
            var receivedValueCount: Int32 = 0
            // .values requests a single value at a time with a demand of 1
            for try await _ in publisher.values {
                let emittedCount = integrationTests.emittedCount
                XCTAssert(emittedCount == receivedValueCount, "Back pressure isn't applied")
                delay(0.2)
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Publisher should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testNilValueReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let nullValueIndex = randomInt(min: 0, max: sendValueCount - 1)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100)))
        #else
        let publisher = createPublisher(for: integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100))
        #endif
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
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                XCTAssertEqual(value.flatMap(Int.init), receivedValueCount, "Received incorrect value")
                #else
                XCTAssertEqual(value?.intValue, receivedValueCount, "Received incorrect value")
                #endif
            }
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
    
    func testExceptionReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let exceptionIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100)))
        #else
        let publisher = createPublisher(for: integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(exceptionIndex)
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = publisher.sink { completion in
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
                XCTFail("Publisher should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testErrorReceived() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let errorIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100)))
        #else
        let publisher = createPublisher(for: integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = Int(errorIndex)
        let completionExpectation = expectation(description: "Waiting for completion")
        let cancellable = publisher.sink { completion in
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
                XCTFail("Publisher should complete with an error")
            }
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        _ = cancellable // This is just to remove the unused variable warning
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation, completionExpectation], timeout: 4)
        delay(1) // Delay is needed else the job isn't completed yet
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testNotOnMainThread() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlow(count: 1, delay: 1000)))
        #else
        let publisher = createPublisher(for: integrationTests.getFlow(count: 1, delay: 1000))
        #endif
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
        wait(for: [valueExpectation, completionExpectation], timeout: 3)
    }
    
    func testCancellation() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let callbackExpectation = expectation(description: "Waiting for callback not to get called")
        callbackExpectation.isInverted = true
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 3, delay: 1000) {
            callbackExpectation.fulfill()
        }))
        #else
        let publisher = createPublisher(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 3, delay: 1000) {
            callbackExpectation.fulfill()
        })
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = 2
        let completionExpectation = expectation(description: "Waiting for completion")
        completionExpectation.isInverted = true
        let cancellable = publisher.sink { _ in
            completionExpectation.fulfill()
        } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        delay(0.1) // Gives the job some time to start (in Swift Export)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
        wait(for: [valuesExpectation], timeout: 4)
        XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
        cancellable.cancel()
        XCTAssertEqual(integrationTests.activeJobCount, 0, "The job shouldn't be active anymore")
        wait(for: [callbackExpectation, completionExpectation], timeout: 2)
        XCTAssertEqual(integrationTests.uncompletedJobCount, 0, "The job should have completed by now")
    }
    
    func testThreadLock() {
        let integrationTests = setup(KotlinThreadLockIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.stateFlow))
        #else
        let publisher = createPublisher(for: integrationTests.stateFlow)
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = 3
        let cancellable = publisher.sink { _ in } receiveValue: { _ in
            valuesExpectation.fulfill()
        }
        wait(for: [valuesExpectation], timeout: 6)
        cancellable.cancel()
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Unit values don't work yet, see https://youtrack.jetbrains.com/issue/KT-85163
    func testUnitValues() {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let publisher = createPublisher(for: asyncSequence(for: integrationTests.getUnitFlow(count: 2, delay: 100)))
        #else
        let publisher = createPublisher(for: integrationTests.getUnitFlow(count: 2, delay: 100))
        #endif
        let valuesExpectation = expectation(description: "Waiting for values")
        valuesExpectation.expectedFulfillmentCount = 2
        let completionExpectation = expectation(description: "Waiting for completion")
        var receivedValueCount = 0
        let cancellable = publisher.sink { completion in
            if case .failure(_) = completion {
                XCTFail("Publisher should complete without an error")
            }
            completionExpectation.fulfill()
        } receiveValue: {
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
}
