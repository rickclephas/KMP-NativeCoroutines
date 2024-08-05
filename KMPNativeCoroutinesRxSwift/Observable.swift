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
public func createObservable<Failure: Error>(
    for nativeFlow: @escaping NativeFlow<NativeUnit?, Failure>
) -> Observable<Void> {
    return createObservableImpl(for: nativeFlow).map { _ in }
}

private func createObservableImpl<Output, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Output, Failure>
) -> Observable<Output> {
    if let observable = nativeFlow(RETURN_TYPE_RXSWIFT_OBSERVABLE, EmptyNativeCallback2, EmptyNativeCallback1, EmptyNativeCallback1)() {
        return observable as! Observable<Output>
    }
    return Observable.deferred {
        return Observable.create { observer in
            let nativeCancellable = nativeFlow(nil, { item, next in
                observer.onNext(item)
                return next()
            }, { error in
                if let error = error {
                    observer.onError(error)
                } else {
                    observer.onCompleted()
                }
                return nil
            }, { cancellationError in
                observer.onError(cancellationError)
                return nil
            })
            return Disposables.create { _ = nativeCancellable() }
        }
    }
}
