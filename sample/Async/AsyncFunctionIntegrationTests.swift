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
    
    override func setUp() {
        CoroutinesAppleKt.doInitCoroutinesFromMainThread()
    }
    
    func testValueReceived() async throws {
        let integrationTests = SuspendIntegrationTests()
        let sendValue = randomInt()
        let value = try await asyncFunction(for: integrationTests.returnValueNative(value: sendValue, delay: 1000))
        XCTAssertEqual(value.int32Value, sendValue, "Received incorrect value")
        await assertJobCompleted(integrationTests)
    }
    
    func testNilValueReceived() async throws {
        let integrationTests = SuspendIntegrationTests()
        let value = try await asyncFunction(for: integrationTests.returnNullNative(delay: 1000))
        XCTAssertNil(value, "Value should be nil")
        await assertJobCompleted(integrationTests)
    }
    
    func testExceptionReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        do {
            _ = try await asyncFunction(for: integrationTests.throwExceptionNative(message: sendMessage, delay: 1000))
            XCTFail("Function should complete with an error")
        } catch {
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinException, "Error doesn't contain the Kotlin exception")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testErrorReceived() async {
        let integrationTests = SuspendIntegrationTests()
        let sendMessage = randomString()
        do {
            _ = try await asyncFunction(for: integrationTests.throwErrorNative(message: sendMessage, delay: 1000))
            XCTFail("Function should complete with an error")
        } catch {
            let error = error as NSError
            XCTAssertEqual(error.localizedDescription, sendMessage, "Error has incorrect localizedDescription")
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinThrowable, "Error doesn't contain the Kotlin error")
        }
        await assertJobCompleted(integrationTests)
    }
    
    func testCancellation() async {
        let integrationTests = SuspendIntegrationTests()
        let handle = Task {
            return try await asyncFunction(for: integrationTests.returnFromCallbackNative(delay: 3000) {
                XCTFail("Callback shouldn't be invoked")
                return KotlinInt(int: 1)
            })
        }
        DispatchQueue.global().asyncAfter(deadline: .now() + 1) {
            XCTAssertEqual(integrationTests.activeJobCount, 1, "There should be 1 active job")
            handle.cancel()
        }
        let result = await handle.result
        await assertJobCompleted(integrationTests)
        if case let .failure(error) = result {
            let error = error as NSError
            let exception = error.userInfo["KotlinException"]
            XCTAssertTrue(exception is KotlinCancellationException, "Error should be a KotlinCancellationException")
        } else {
            XCTFail("Function should fail with an error")
        }
    }
}
