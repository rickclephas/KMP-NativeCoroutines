//
//  AsyncResultIntegrationTests.swift
//  Sample
//
//  Created by Rick Clephas on 28/06/2021.
//

import XCTest
import KMPNativeCoroutinesAsync
import NativeCoroutinesSampleShared

class AsyncResultIntegrationTests: XCTestCase {
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testValueReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendValue = randomInt()
        let result = await asyncResult(for: integrationTests.returnValue(value: sendValue, delay: 1000))
        guard case let .success(value) = result else {
            XCTFail("Function should complete without an error")
            return
        }
        XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testNilValueReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let result = await asyncResult(for: integrationTests.returnNull(delay: 1000))
        guard case let .success(value) = result else {
            XCTFail("Function should complete without an error")
            return
        }
        XCTAssertNil(value, "Value should be nil")
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testExceptionReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        let result = await asyncResult(for: integrationTests.throwException(message: sendMessage, delay: 1000))
        guard case let .failure(error) = result else {
            XCTFail("Function should complete with an error")
            return
        }
        let nsError = error as NSError
        XCTAssertEqual(nsError.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
        let exception = nsError.userInfo["KotlinException"]
        XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testErrorReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        let result = await asyncResult(for: integrationTests.throwError(message: sendMessage, delay: 1000))
        guard case let .failure(error) = result else {
            XCTFail("Function should complete with an error")
            return
        }
        let nsError = error as NSError
        XCTAssertEqual(nsError.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
        let exception = nsError.userInfo["KotlinException"]
        XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
        await assertJobCompleted(integrationTests)
    }
    #endif
    
    #if !NATIVE_COROUTINES_SWIFT_EXPORT
    func testCancellation() async {
        let integrationTests = SuspendIntegrationTests()
        let handle = Task {
            return await asyncResult(for: integrationTests.returnFromCallback(delay: 3000) {
                XCTFail("Callback shouldn't be invoked")
                return KotlinInt(int: 1)
            })
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
            handle.cancel()
        }
        let handleResult = await handle.result
        await assertJobCompleted(integrationTests)
        guard case let .success(result) = handleResult else {
            XCTFail("Task should complete without an error")
            return
        }
        if case let .failure(error) = result {
            XCTAssertTrue(error is CancellationError, "Error should be a CancellationError")
        } else {
            XCTFail("Function should fail with an error")
        }
    }
    #endif
    
//    #if !NATIVE_COROUTINES_SWIFT_EXPORT
//    func testUnitReturnType() async throws {
//        let integrationTests = SuspendIntegrationTests()
//        let result = await asyncResult(for: integrationTests.returnUnit(delay: 100))
//        guard case .success = result else {
//            XCTFail("Function should complete without an error")
//            return
//        }
//        await assertJobCompleted(integrationTests)
//    }
//    #endif
}
