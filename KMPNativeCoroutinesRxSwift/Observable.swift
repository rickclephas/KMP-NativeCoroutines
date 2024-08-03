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
    return createObservableImpl(for: nativeFlow)
}

/// Creates an `Observable` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Unit, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Unit, Failure, Unit>
) -> Observable<Void> {
    return createObservableImpl(for: nativeFlow).map { _ in }
}

private func createObservableImpl<Output, Failure: Error>(
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
