//
//  AsyncFunction.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 13/06/2021.
//

import KMPNativeCoroutinesCore

/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The result from the `nativeSuspend`.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) async throws -> Result {
    for try await result in asyncStream(for: nativeSuspend) {
        return result
    }
    try Task.checkCancellation()
    fatalError("NativeSuspend should always return a value")
}

private func asyncStream<Result, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>
) -> AsyncThrowingStream<Result, Error> {
    return AsyncThrowingStream<Result, Error> { continuation in
        let nativeCancellable = nativeSuspend({ output, unit in
            continuation.yield(output)
            continuation.finish()
            return unit
        }, { error, unit in
            continuation.finish(throwing: error)
            return unit
        })
        continuation.onTermination = { @Sendable termination in
            guard case .cancelled = termination else { return }
            _ = nativeCancellable()
        }
    }
}
