//
//  AsyncFunctionIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 14/06/2021.
//

import XCTest
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared
#if NATIVE_COROUTINES_SWIFT_EXPORT
import KotlinRuntimeSupport
#endif

class AsyncFunctionIntegrationTests: XCTestCase {
    
    func testValueReceived() async throws {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendValue = randomInt()
        let value = try await asyncFunction(for: integrationTests.returnValue(value: sendValue, delay: 1000))
        #if NATIVE_COROUTINES_SWIFT_EXPORT
        XCTAssertEqual(value, sendValue, "Received incorrect value")
        #else
        XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
        #endif
        await assertJobCompleted(integrationTests)
    }
    
    func testNilValueReceived() async throws {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let value = try await asyncFunction(for: integrationTests.returnNull(delay: 1000))
        XCTAssertNil(value, "Value should be nil")
        await assertJobCompleted(integrationTests)
    }
    
    func testExceptionReceived() async {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendMessage = randomString()
        do {
            _ = try await asyncFunction(for: integrationTests.throwException(message: sendMessage, delay: 1000))
            XCTFail("Function should complete with an error")
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
    }
    
    func testErrorReceived() async {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let sendMessage = randomString()
        do {
            _ = try await asyncFunction(for: integrationTests.throwError(message: sendMessage, delay: 1000))
            XCTFail("Function should complete with an error")
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
    }
    
    func testCancellation() async {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        let handle = Task {
            return try await asyncFunction(for: integrationTests.returnFromCallback(delay: 3000) {
                XCTFail("Callback shouldn't be invoked")
                #if NATIVE_COROUTINES_SWIFT_EXPORT
                return 1
                #else
                return KotlinInt(int: 1)
                #endif
            })
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
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
    
    func testUnitReturnType() async throws {
        let integrationTests = setup(KotlinSuspendIntegrationTests.init)
        try await asyncFunction(for: integrationTests.returnUnit(delay: 100))
        await assertJobCompleted(integrationTests)
    }
}
