//
//  AsyncStream.swift
//  AsyncStream
//
//  Created by Rick Clephas on 15/07/2021.
//

import KMPNativeCoroutinesCore

/// Wraps the `NativeFlow` in an `AsyncThrowingStream`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An stream that yields the collected values.
public func asyncStream<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> AsyncThrowingStream<Output, Error> {
    return AsyncThrowingStream { continuation in
        let nativeCancellable = nativeFlow({ item, unit in
            continuation.yield(item)
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
