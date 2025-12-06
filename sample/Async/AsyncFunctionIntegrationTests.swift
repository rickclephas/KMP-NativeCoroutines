//
//  AsyncFunctionIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared

class AsyncFunctionIntegrationTests: XCTestCase {
    
    func testValueReceived() async throws {
        let integrationTests = KotlinSuspendIntegrationTests()
        let sendValue = randomInt()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let value = try await asyncFunction(for: integrationTests.returnValueNative(value: sendValue, delay: 1000))
        XCTAssertEqual(value, sendValue, "Received incorrect value")
        #else
        let value = try await asyncFunction(for: integrationTests.returnValue(value: sendValue, delay: 1000))
        XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
        #endif
        await assertJobCompleted(integrationTests)
    }
    
    func testNilValueReceived() async throws {
        let integrationTests = KotlinSuspendIntegrationTests()
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        let value = try await asyncFunction(for: integrationTests.returnNullNative(delay: 1000))
        #else
        let value = try await asyncFunction(for: integrationTests.returnNull(delay: 1000))
        #endif
        XCTAssertNil(value, "Value should be nil")
        await assertJobCompleted(integrationTests)
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Exception throwing isn't supported yet, see https://youtrack.jetbrains.com/issue/KT-80971
    func testExceptionReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        do {
            _ = try await asyncFunction(for: integrationTests.throwException(message: sendMessage, delay: 1000))
            XCTFail("Function should complete with an error")
        } catch {
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
        }
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Exception throwing isn't supported yet, see https://youtrack.jetbrains.com/issue/KT-80971
    func testErrorReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        do {
            _ = try await asyncFunction(for: integrationTests.throwError(message: sendMessage, delay: 1000))
            XCTFail("Function should complete with an error")
        } catch {
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
        }
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    func testCancellation() async {
        let integrationTests = KotlinSuspendIntegrationTests()
        let handle = Task {
            #if NATIVE_COROUTINES_SWIFT_EXPORT
            return try await asyncFunction(for: integrationTests.returnFromCallbackNative(delay: 3000) {
                XCTFail("Callback shouldn't be invoked")
                return 1
            })
            #else
            return try await asyncFunction(for: integrationTests.returnFromCallback(delay: 3000) {
                XCTFail("Callback shouldn't be invoked")
                return KotlinInt(int: 1)
            })
            #endif
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            #if !NATIVE_COROUTINES_SWIFT_EXPORT
            XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
            #endif
            handle.cancel()
        }
        let result = await handle.result
        await assertJobCompleted(integrationTests)
        if case let .failure(error) = result {
            XCTAssertTrue(error is CancellationError, "Error should be a CancellationError")
        } else {
            XCTFail("Function should fail with an error")
        }
    }
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    /// Suspend functions returning Unit aren't supported yet, see https://youtrack.jetbrains.com/issue/KT-81593
    func testUnitReturnType() async throws {
        let integrationTests = SuspendIntegrationTests()
        try await asyncFunction(for: integrationTests.returnUnit(delay: 100))
        await assertJobCompleted(integrationTests)
    }
    #endif
}
