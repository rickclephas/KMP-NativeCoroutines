//
//  SingleTests.swift
//  KMPNativeCoroutinesRxSwiftTests
//
//  Created by Rick Clephas on 05/07/2021.
//

import XCTest
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesRxSwift

class SingleTests: XCTestCase {
    
    private class TestValue { }

    func testDisposableInvoked() {
        var cancelCount = 0
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, _, _, _ in
            guard returnType == nil else { return { nil } }
            return {
                cancelCount += 1
                return nil
            }
        }
        let disposable = createSingle(for: nativeSuspend).subscribe()
        XCTAssertEqual(cancelCount, 0, "Disposable shouldn't be invoked yet")
        disposable.dispose()
        XCTAssertEqual(cancelCount, 1, "Disposable should be invoked once")
    }
    
    func testCompletionWithValue() {
        let value = TestValue()
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, resultCallback, _, _ in
            guard returnType == nil else { return { nil } }
            _ = resultCallback(value, ())
            return { nil }
        }
        var successCount = 0
        let disposable = createSingle(for: nativeSuspend)
            .subscribe(onSuccess: { receivedValue in
                XCTAssertIdentical(receivedValue, value, "Received incorrect value")
                successCount += 1
            }, onFailure: { _ in
                XCTFail("Single should complete without error")
            })
        _ = disposable // This is just to remove the unused variable warning
        XCTAssertEqual(successCount, 1, "Success closure should be called once")
    }
    
    func testCompletionWithError() {
        let error = NSError(domain: "Test", code: 0)
        let nativeSuspend: NativeSuspend<TestValue, NSError> = { returnType, _, errorCallback, _ in
            guard returnType == nil else { return { nil } }
            _ = errorCallback(error, ())
            return { nil }
        }
        var failureCount = 0
        let disposable = createSingle(for: nativeSuspend)
            .subscribe(onSuccess: { _ in
                XCTFail("Single should complete with an error")
            }, onFailure: { receivedError in
                XCTAssertIdentical(receivedError as NSError, error, "Received incorrect error")
                failureCount += 1
            })
        _ = disposable // This is just to remove the unused variable warning
        XCTAssertEqual(failureCount, 1, "Failure closure should be called once")
    }
}
