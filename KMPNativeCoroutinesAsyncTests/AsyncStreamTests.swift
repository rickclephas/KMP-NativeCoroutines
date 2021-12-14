//
//  AsyncStreamTests.swift
//  AsyncStreamTests
//
//  Created by Rick Clephas on 15/07/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesAsync

class AsyncStreamTests: XCTestCase {
    
    private class TestValue { }

    func testCancellableInvoked() async {
        var cancelCount = 0
        let nativeFlow: NativeFlow<TestValue, NSError, Void> = { _, _ in
            return { cancelCount += 1 }
        }
        let handle = Task {
            for try await _ in asyncStream(for: nativeFlow) { }
        }
        XCTAssertEqual(cancelCount, 0, "Cancellable shouldn't be invoked yet")
        handle.cancel()
        let result = await handle.result
        XCTAssertEqual(cancelCount, 1, "Cancellable should be invoked once")
        guard case .success(_) = result else {
            XCTFail("Task should complete without an error")
            return
        }
    }
    
    func testCompletionWithCorrectValues() async {
        let values = [TestValue(), TestValue(), TestValue(), TestValue(), TestValue()]
        let nativeFlow: NativeFlow<TestValue, NSError, Void> = { itemCallback, completionCallback in
            for value in values {
                itemCallback(value, ())
            }
            completionCallback(nil, ())
            return { }
        }
        var valueCount = 0
        do {
            for try await receivedValue in asyncStream(for: nativeFlow) {
                XCTAssertIdentical(receivedValue, values[valueCount], "Received incorrect value")
                valueCount += 1
            }
        } catch {
            XCTFail("Stream should complete without error")
        }
        XCTAssertEqual(valueCount, values.count, "All values should be received")
    }
    
    func testCompletionWithError() async {
        let sendError = NSError(domain: "Test", code: 0)
        let nativeFlow: NativeFlow<TestValue, NSError, Void> = { _, completionCallback in
            completionCallback(sendError, ())
            return { }
        }
        var valueCount = 0
        do {
            for try await _ in asyncStream(for: nativeFlow) {
                valueCount += 1
            }
            XCTFail("Stream should complete with an error")
        } catch {
            XCTAssertEqual(error as NSError, sendError, "Received incorrect error")
        }
        XCTAssertEqual(valueCount, 0, "No values should be received")
    }
}
