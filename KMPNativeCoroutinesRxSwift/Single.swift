//
//  Single.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 05/07/2021.
//

import RxSwift
import KMPNativeCoroutinesCore

/// Creates a `Single` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A single that either finishes with a single value or fails with an error.
@available(*, deprecated)
public func createSingle<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) -> Single<Result> {
    return createSingleImpl(for: nativeSuspend)
}

public func createSingle<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping () -> NativeSuspend<Result, Failure, Unit>
) -> Single<Result> {
    return createSingleImpl(for: nativeSuspend())
}

@available(*, deprecated)
public func createSingle<Result>(for result: @escaping () async throws -> Result) -> Single<Result> {
    return Single.deferred {
        return Single.create { observer in
            let handle = Task {
                do {
                    observer(.success(try await result()))
                } catch {
                    observer(.failure(error))
                }
            }
            return Disposables.create { handle.cancel() }
        }
    }
}

/// Creates a `Single` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A single that either finishes with a single value or fails with an error.
public func createSingle<Unit, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<Unit, Failure, Unit>
) -> Single<Void> {
    return createSingleImpl(for: nativeSuspend).map { _ in }
}

private func createSingleImpl<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) -> Single<Result> {
    return Single.deferred {
        return Single.create { observer in
            let nativeCancellable = nativeSuspend({ output, unit in
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
