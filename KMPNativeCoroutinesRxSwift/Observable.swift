//
//  Observable.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 05/07/2021.
//

import RxSwift
import KMPNativeCoroutinesCore

/// Creates an `Observable` for the provided `NativeFlow`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> Observable<Output> {
    return Observable.deferred {
        return Observable.create { observer in
            let nativeCancellable = nativeFlow({ item, next, _ in
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
