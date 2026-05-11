//
//  FuturePublisher.swift
//  KMPNativeCoroutinesCombine
//
//  Created by Rick Clephas on 28/06/2021.
//

import Combine
import KMPNativeCoroutinesCore

/// Creates an `AnyPublisher` for the provided `NativeSuspend` that returns a `NativeFlow`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Output, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<Output, Failure, Unit>, Failure, Unit>
) -> AnyPublisher<Output, Failure> {
    return createFuture(for: nativeSuspend)
        .flatMap { createPublisher(for: $0) }
        .eraseToAnyPublisher()
}

/// This function provides source compatibility during the migration to Swift export.
///
/// You should migrate away from this function once you have fully migrated to Swift export.
@available(*, deprecated, message: "Kotlin Coroutines are supported by Swift export")
public func createPublisher<Sequence: AsyncSequence>(
    for asyncSequence: @escaping () async throws -> Sequence
) -> AnyPublisher<Sequence.Element, Error> {
    return createFuture(for: asyncSequence)
        .flatMap { createPublisher(for: $0) }
        .eraseToAnyPublisher()
}

/// Creates an `AnyPublisher` for the provided `NativeSuspend` that returns a `NativeFlow`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Unit, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<Unit, Failure, Unit>, Failure, Unit>
) -> AnyPublisher<Void, Failure> {
    return createFuture(for: nativeSuspend)
        .flatMap { createPublisher(for: $0) }
        .eraseToAnyPublisher()
}
