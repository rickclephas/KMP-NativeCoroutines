//
//  ObservableTests.swift
//  KMPNativeCoroutinesRxSwiftTests
//
//  Created by Rick Clephas on 05/07/2021.
//

import XCTest
import RxSwift
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesRxSwift

class ObservableTests: XCTestCase {
    
    private class TestValue { }

    func testDisposableInvoked() {
        var cancelCount = 0
        let nativeFlow: NativeFlow<TestValue, NSError> = { returnType, _, _, _ in
            guard returnType == nil else { return { nil } }
            return {
                cancelCount += 1
                return nil
            }
        }
        let disposable = createObservable(for: nativeFlow).subscribe()
        XCTAssertEqual(cancelCount, 0, "Disposable shouldn't be invoked yet")
        disposable.dispose()
        XCTAssertEqual(cancelCount, 1, "Disposable should be invoked once")
    }
    
    func testCompletionWithCorrectValues() {
        let values = [TestValue(), TestValue(), TestValue(), TestValue(), TestValue()]
        let nativeFlow: NativeFlow<TestValue, NSError> = { returnType, itemCallback, completionCallback, _ in
            guard returnType == nil else { return { nil } }
            for value in values {
                _ = itemCallback(value, {}, ())
            }
            _ = completionCallback(nil, ())
            return { nil }
        }
        var completionCount = 0
        var valueCount = 0
        let disposable = createObservable(for: nativeFlow)
            .subscribe(onNext: { receivedValue in
                XCTAssertIdentical(receivedValue, values[valueCount], "Received incorrect value")
                valueCount += 1
            }, onError: { error in
                XCTFail("Observable should complete without error")
            }, onCompleted: {
                completionCount += 1
            })
        _ = disposable // This is just to remove the unused variable warning
        XCTAssertEqual(completionCount, 1, "Completion closure should be called once")
        XCTAssertEqual(valueCount, values.count, "Value closure should be called for every value")
    }
    
    func testCompletionWithError() {
        let error = NSError(domain: "Test", code: 0)
        let nativeFlow: NativeFlow<TestValue, NSError> = { returnType, _, completionCallback, _ in
            guard returnType == nil else { return { nil } }
            _ = completionCallback(error, ())
            return { nil }
        }
        var errorCount = 0
        var valueCount = 0
        let disposable = createObservable(for: nativeFlow)
            .subscribe(onNext: { _ in
                valueCount += 1
            }, onError: { receivedError in
                XCTAssertIdentical(receivedError as NSError, error, "Received incorrect error")
                errorCount += 1
            }, onCompleted: {
                XCTFail("Observable should complete with an error")
            })
        _ = disposable // This is just to remove the unused variable warning
        XCTAssertEqual(errorCount, 1, "Error closure should be called once")
        XCTAssertEqual(valueCount, 0, "Value closure shouldn't be called")
    }
    
    func testObservableIsOriginal() {
        let nativeFlow = Observable.just(TestValue()).asNativeFlow()
        _ = createObservable { returnType, onItem, onComplete, onCancelled in
            if let returnType {
                return nativeFlow(returnType, onItem, onComplete, onCancelled)
            }
            XCTFail("Observable should be returned")
            return { nil }
        }
    }
}
