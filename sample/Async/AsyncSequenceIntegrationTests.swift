//
//  AsyncSequenceIntegrationTests.swift
//  AsyncSequenceIntegrationTests
//
//  Created by Rick Clephas on 06/03/2022.
//

import XCTest
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared
#if NATIVE_COROUTINES_SWIFT_EXPORT
import KotlinRuntimeSupport
#endif

class AsyncSequenceIntegrationTests: XCTestCase {
    
    func testValuesReceived() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let sequence = integrationTests.getFlow(count: sendValueCount, delay: 100).asAsyncSequence()
        #else
        let sequence = asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        #endif
        do {
            var receivedValueCount: Int32 = 0
            for try await value in sequence {
                if receivedValueCount + 1 < sendValueCount {
                    // Depending on the timing the job might already have completed for the last value,
                    // so we won't check this for the last value but only for earlier values
                    XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                }
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                XCTAssertEqual(value, receivedValueCount, "Received incorrect value")
                #else
                XCTAssertEqual(value.int32Value, receivedValueCount, "Received incorrect value")
                #endif
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testValueBackPressure() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount: Int32 = 10
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let sequence = integrationTests.getFlow(count: sendValueCount, delay: 100).asAsyncSequence()
        #else
        let sequence = asyncSequence(for: integrationTests.getFlow(count: sendValueCount, delay: 100))
        #endif
        do {
            var receivedValueCount: Int32 = 0
            for try await _ in sequence {
                let emittedCount = integrationTests.emittedCount
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                XCTAssert(emittedCount == receivedValueCount, "Back pressure isn't applied")
                #else
                // Note the AsyncSequence buffers at most a single item
                XCTAssert(emittedCount == receivedValueCount || emittedCount == receivedValueCount + 1, "Back pressure isn't applied")
                #endif
                delay(0.2)
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Nil values in Flow cancel the collection, see http://youtrack.jetbrains.com/issue/KT-84485
    func testNilValueReceived() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let nullValueIndex = randomInt(min: 0, max: sendValueCount - 1)
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let sequence = integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100).asAsyncSequence()
        #else
        let sequence = asyncSequence(for: integrationTests.getFlowWithNull(count: sendValueCount, nullIndex: nullValueIndex, delay: 100))
        #endif
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
                    #if NATIVE_COROUTINES_SWIFT_EXPORT
                    XCTAssertEqual(value, receivedValueCount, "Received incorrect value")
                    #else
                    XCTAssertEqual(value?.int32Value, receivedValueCount, "Received incorrect value")
                    #endif
                }
                receivedValueCount += 1
            }
            XCTAssertEqual(receivedValueCount, sendValueCount, "Should have received all values")
        } catch {
            XCTFail("Sequence should complete without an error")
        }
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    func testExceptionReceived() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let exceptionIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let sequence = integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100).asAsyncSequence()
        #else
        let sequence = asyncSequence(for: integrationTests.getFlowWithException(count: sendValueCount, exceptionIndex: exceptionIndex, message: sendMessage, delay: 100))
        #endif
        var receivedValueCount: Int32 = 0
        do {
            for try await _ in sequence {
                XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                receivedValueCount += 1
            }
            XCTFail("Sequence should fail with an error")
        } catch {
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
        }
        await assertJobCompleted(integrationTests)
        XCTAssertEqual(receivedValueCount, exceptionIndex, "Should have received all values before the exception")
    }
    
    func testErrorReceived() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let sendValueCount = randomInt(min: 5, max: 20)
        let errorIndex = randomInt(min: 1, max: sendValueCount - 1)
        let sendMessage = randomString()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let sequence = integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100).asAsyncSequence()
        #else
        let sequence = asyncSequence(for: integrationTests.getFlowWithError(count: sendValueCount, errorIndex: errorIndex, message: sendMessage, delay: 100))
        #endif
        var receivedValueCount: Int32 = 0
        do {
            for try await _ in sequence {
                XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                receivedValueCount += 1
            }
            XCTFail("Sequence should fail with an error")
        } catch {
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
        }
        await assertJobCompleted(integrationTests)
        XCTAssertEqual(receivedValueCount, errorIndex, "Should have received all values before the error")
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Cancellation isn't working yet, see https://youtrack.jetbrains.com/issue/KT-85159
    func testCancellation() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let handle = Task<Void, Never> {
            do {
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                let sequence = integrationTests.getFlowWithCallback(count: 5, callbackIndex: 3, delay: 1000) {
                    XCTFail("The callback shouldn't be called")
                }.asAsyncSequence()
                #else
                let sequence = asyncSequence(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 3, delay: 1000) {
                    XCTFail("The callback shouldn't be called")
                })
                #endif
                for try await _ in sequence {
                    XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
                }
            } catch {
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                XCTAssertTrue(error is CancellationError, "Error should be a CancellationError")
                #else
                XCTFail("Sequence should be cancelled without an error")
                #endif
            }
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + 2) {
            XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
            handle.cancel()
        }
        await handle.value
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Cancellation isn't working yet, see https://youtrack.jetbrains.com/issue/KT-85159
    func testImplicitCancellation() async {
        let integrationTests = setup(KotlinFlowIntegrationTests.init)
        let handle = Task<Void, Never> {
            do {
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                let sequence = integrationTests.getFlowWithCallback(count: 5, callbackIndex: 2, delay: 1000) {
                    XCTFail("The callback shouldn't be called")
                }.asAsyncSequence()
                #else
                let sequence = asyncSequence(for: integrationTests.getFlowWithCallback(count: 5, callbackIndex: 2, delay: 1000) {
                    XCTFail("The callback shouldn't be called")
                })
                #endif
                let iterator = sequence.makeAsyncIterator()
                let _ = try await iterator.next()
                XCTAssertEqual(integrationTests.uncompletedJobCount, 1, "There should be 1 uncompleted job")
            } catch {
                XCTFail("Sequence should be cancelled without an error")
            }
        }
        await handle.value
        await assertJobCompleted(integrationTests)
    }
    #endif
}
