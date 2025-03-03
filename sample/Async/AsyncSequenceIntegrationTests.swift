//
//  AsyncSequenceIntegrationTests.swift
//  AsyncSequenceIntegrationTests
//
//  Created by Rick Clephas on 06/03/2022.
//

import XCTest
import KMPNativeCoroutinesAsync
import KMPNativeCoroutinesCore
@preconcurrency import NativeCoroutinesSampleShared

class AsyncSequenceIntegrationTests: XCTestCase {
    
    func testValuesReceived() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let sequence = asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
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
    
    func testValueBackPressure() async throws {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount: Int32 = 10
        let sequence = asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        do {
            var receivedValueCount: Int32 = 0
            // This will emit a value and return on the same queue which is the expected behaviour of async streams
            // Old implementation returned on cooporative queue making the consumption of the sample not block the emit. This is not expected for async streams.
            var result: [String] = []
            for try await value in sequence {
                // Note the AsyncSequence buffers at most a single item
                result.append("\(value)")
                delay(0.2) // This effectively blocks the emit as is expected for async streams. Due to the build in buffer policy of AysyncThrowing stream by default nothing is lost
                receivedValueCount += 1
            }
            XCTAssertEqual(result, [
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9"
            ], """
            Should have received all values. You might notice that the middle, the emittedCount skips a beat.
            This is normal als the delay here block the same caller queue until it is ready to receive another value.
            When it is ready the AsyncThrowingStream emits a value from its, by default infinate, buffer. Hence effectively
            allowing for all values to be passed into swift. This might not be abovious from just testing the emit count as that one skips a beat.
            But in this case is the expected behaviour if you align with how streams behave in swift structured concurrency world.
            """)
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testValueBackPressureDelibaratlyLoosesValues() async throws {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount: Int32 = 10
        // Delibarately disable buffer policy, sometimes wanted in real time data processing or when processing audio streams that have to be real time.
        // In that case you would want buffers to be dropped when consumption is too slow. With AsyncThrowingStream you have that behaviour as an option
        // with the older implementation `NativeFlowAsyncSequence` the result is returned on a cooparative queue making it async to the sample stream. Which
        // you might not want.
        let sequence = asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100), bufferingPolicy: .bufferingNewest(0))
        do {
            // This will emit a value and return on the same queue which is the expected behaviour of async streams
            // Old implementation returned on cooporative queue making the consumption of the sample not block the emit. This is not expected for async streams.
            var result: [String] = []
            for try await value in sequence {
                // Note the AsyncSequence buffers at most a single item
                result.append("\(value)")
                delay(0.2) // This effectively blocks the emit as is expected for async streams. Due to the build in buffer policy of AysyncThrowing stream by default nothing is lost
            }
            
            XCTAssertTrue(result.count < 10, """
            This test shows that you have the option to force drops of items if consumption is too slow.
            The values dropped are a bit random so a test with exact values is not possible. But it should be less then
            10 values or the buffer is still active.
            """)
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testValueBackPressureWhenConsumptionIsAsync() async throws {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount: Int32 = 10
       
        let sequence = asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100), bufferingPolicy: .bufferingNewest(0))
   
        do {
            var result: [String] = []
            for try await value in sequence {
                try await Task{
                    result.append("\(value)")
                    try await Task.sleep(nanoseconds: 200)
                }.value
            }
            
            XCTAssertEqual(result, [
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9"], """
            This will now correctly count for emit and not use the buffer policy but still have all the elements as 
            consumption is async.
            """)
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    
    func testNilValueReceived() async {
        let integrationTests = FlowIntegrationTests()
        let sendValueCount = randomInt(min: 5, max: 20)
        let nullValueIndex = randomInt(min: 0, max: sendValueCount - 1)
        let sequence = asyncSequence(for: integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100))
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
        let sequence = asyncSequence(for: integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100))
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
        let sequence = asyncSequence(for: integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100))
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
                let sequence = asyncSequence(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 2, delay: 1000) {
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
