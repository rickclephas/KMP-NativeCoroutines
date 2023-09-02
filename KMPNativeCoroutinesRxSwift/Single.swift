//
//  Single.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 05/07/2021.
//

import RxSwift
import KMPNativeCoroutinesCore

internal let RETURN_TYPE_RXSWIFT_SINGLE = "rxswift-single"

/// Creates a `Single` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A single that either finishes with a single value or fails with an error.
public func createSingle<Result, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure>
) -> Single<Result> {
    if let single = nativeSuspend(RETURN_TYPE_RXSWIFT_SINGLE, EmptyNativeCallback, EmptyNativeCallback, EmptyNativeCallback)() {
        return single as! Single<Result>
    }
    return Single.deferred {
        return Single.create { observer in
            let nativeCancellable = nativeSuspend(nil, { output, unit in
                observer(.success(output))
                return unit
            }, { error, unit in
                observer(.failure(error))
                return unit
            }, { cancellationError, unit in
                observer(.failure(cancellationError))
                return unit
            })
            return Disposables.create { _ = nativeCancellable() }
        }
    }
}

public extension Single where Trait == SingleTrait {
    /// Creates a `NativeSuspend` for this `Single`.
    func asNativeSuspend() -> NativeSuspend<Element, Error> {
        return { returnType, onResult, onError, onCancelled in // TODO: Use onCancelled
            if returnType == RETURN_TYPE_RXSWIFT_SINGLE {
                return { self }
            } else if returnType != nil {
                return { nil }
            }
            let disposable = self.subscribe(onSuccess: { value in
                _ = onResult(value, ())
            }, onFailure: { error in
                _ = onError(error, ())
            })
            return disposable.asNativeCancellable()
        }
    }
}
