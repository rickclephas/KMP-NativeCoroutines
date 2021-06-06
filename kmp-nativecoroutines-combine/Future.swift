//
//  Future.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 06/06/2021.
//

import Combine
import KMPNativeCoroutinesSwift

/// Creates an `AnyPublisher` for the provided `NativeSuspend`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that either finishes with a single value or fails with an error.
public func createFuture<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) -> AnyPublisher<Result, Failure> {
    return Deferred<Publishers.HandleEvents<Future<Result, Failure>>> {
        var cancel: NativeCancellable<Unit>? = nil
        return Future { promise in
            cancel = nativeSuspend({ output, unit in
                promise(.success(output))
                return unit
            }, { error, unit in
                promise(.failure(error))
                return unit
            })
        }.handleEvents(receiveCancel: {
            _ = cancel?()
        })
    }.eraseToAnyPublisher()
}
