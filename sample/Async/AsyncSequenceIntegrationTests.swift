//
//  AsyncSequenceIntegrationTests.swift
//  AsyncSequenceIntegrationTests
//
//  Created by Rick Clephas on 06/03/2022.
//

import XCTest
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared

class AsyncSequenceIntegrationTests: XCTestCase {
    
    func testValuesReceived() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let sequence = asyncSequence(for: integrationTests.getFlowNative(count: sendValueCount, delay: 100))
        do {
            var receivedValueCount: Int32 = 0
            for try await value in sequence {
                if receivedValueCount + 1 < sendValueCount {
                    // Depending on the timing the job might already have completed for the last value,
                    // so we won't check this for the last value but only for earlier values
                    XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                }
                XCTAssertEqual(value.int32Value, receivedValueCount, "Received incorrect value")
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testValueBackPressure() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount: Int32 = 10
        let sequence = asyncSequence(for: integrationTests.getFlowNative(count: sendValueCount, delay: 100))
        do {
            var receivedValueCount: Int32 = 0
            for try await _ in sequence {
                let emittedCount = integrationTests.emittedCount
                // Note the AsyncSequence buffers at most a single item
                XCTAssert(emittedCount == receivedValueCount || emittedCount == receivedValueCount + 1, "Back pressure isn't applied")
                delay(0.2)
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testNilValueReceived() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let nullValueIndex = randomInt(min: 0, max: sendValueCount - 1)
        let sequence = asyncSequence(for: integrationTests.getFlowWithNullNative(count: sendValueCount, nullIndex: nullValueIndex, delay: 100))
        do {
            var receivedValueCount: Int32 = 0
            for try await value in sequence {
                if receivedValueCount + 1 < sendValueCount {
                    // Depending on the timing the job might already have completed for the last value,
                    // so we won't check this for the last value but only for earlier values
                    XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                }
                if receivedValueCount == nullValueIndex {
                    XCTAssertNil(value, "Value should be nil")
                } else {
                    XCTAssertEqual(value?.int32Value, receivedValueCount, "Received incorrect value")
                }
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testExceptionReceived() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let exceptionIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        let sequence = asyncSequence(for: integrationTests.getFlowWithExceptionNative(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100))
        var receivedValueCount: Int32 = 0
        do {
            for try await _ in sequence {
                XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                receivedValueCount += 1
            }
            XCTFail("Sequence should fail with an error")
        } catch {
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
        }
        await assertJobCompleted(integrationTests)
        XCTAssertEqual(receivedValueCount, exceptionIndex, "Should have received all values before the exception")
    }
    
    func testErrorReceived() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let errorIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        let sequence = asyncSequence(for: integrationTests.getFlowWithErrorNative(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100))
        var receivedValueCount: Int32 = 0
        do {
            for try await _ in sequence {
                XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                receivedValueCount += 1
            }
            XCTFail("Sequence should fail with an error")
        } catch {
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
        }
        await assertJobCompleted(integrationTests)
        XCTAssertEqual(receivedValueCount, errorIndex, "Should have received all values before the error")
    }
    
    func testCancellation() async {
        let integrationTests = FlowIntegrationTests()
        let handle = Task<Void, Never> {
            do {
                let sequence = asyncSequence(for: integrationTests.getFlowWithCallbackNative(count: 5, callbackIndex: 2, delay: 1000) {
                    XCTFail("The callback shouldn't be called")
                })
                for try await _ in sequence {
                    XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                }
            } catch {
                XCTFail("Sequence should be cancelled without an error")
            }
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
            handle.cancel()
        }
        await handle.value
        await assertJobCompleted(integrationTests)
    }
}
