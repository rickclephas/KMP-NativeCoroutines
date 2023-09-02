//
//  Observable.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 05/07/2021.
//

import RxSwift
import KMPNativeCoroutinesCore

internal let RETURN_TYPE_RXSWIFT_OBSERVABLE = "rxswift-observable"

/// Creates an `Observable` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Output, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Output, Failure>
) -> Observable<Output> {
    if let observable = nativeFlow(RETURN_TYPE_RXSWIFT_OBSERVABLE, EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)() {
        return observable as! Observable<Output>
    }
    return Observable.deferred {
        return Observable.create { observer in
            let nativeCancellable = nativeFlow(nil, { item, next, _ in
                observer.onNext(item)
                return next()
            }, { error, unit in
                if let error = error {
                    observer.onError(error)
                } else {
                    observer.onCompleted()
                }
                return unit
            }, { cancellationError, unit in
                observer.onError(cancellationError)
                return unit
            })
            return Disposables.create { _ = nativeCancellable() }
        }
    }
}

public extension Observable {
    /// Creates a `NativeFlow` for this `Observable`.
    func asNativeFlow() -> NativeFlow<Element, Error> {
        return { returnType, onItem, onComplete, onCancelled in // TODO: Use onCancelled
            if returnType == RETURN_TYPE_RXSWIFT_OBSERVABLE {
                return { self }
            } else if returnType != nil {
                return { nil }
            }
            let disposable = self.subscribe(onNext: { value in
                _ = onItem(value, {}, ())
            }, onError: { error in
                _ = onComplete(error, ())
            }, onCompleted: {
                _ = onComplete(nil, ())
            })
            return disposable.asNativeCancellable()
        }
    }
}

