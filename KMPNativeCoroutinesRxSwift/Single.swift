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
    return createSingleImpl(for: nativeSuspend)
}

/// Creates a `Single` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A single that either finishes with a single value or fails with an error.
public func createSingle<Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeUnit?, Failure>
) -> Single<Void> {
    return createSingleImpl(for: nativeSuspend).map { _ in }
}

private func createSingleImpl<Result, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure>
) -> Single<Result> {
    if let single = nativeSuspend(RETURN_TYPE_RXSWIFT_SINGLE, EmptyNativeCallback1, EmptyNativeCallback1, EmptyNativeCallback1)() {
        return single as! Single<Result>
    }
    return Single.deferred {
        return Single.create { observer in
            let nativeCancellable = nativeSuspend(nil, { output in
                observer(.success(output))
                return nil
            }, { error in
                observer(.failure(error))
                return nil
            }, { cancellationError in
                observer(.failure(cancellationError))
                return nil
            })
            return Disposables.create { _ = nativeCancellable() }
        }
    }
}
