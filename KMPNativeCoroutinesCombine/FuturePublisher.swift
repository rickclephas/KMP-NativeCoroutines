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
public func createPublisher<Output, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<Output, Failure>, Failure>
) -> AnyPublisher<Output, Failure> {
    return createFuture(for: nativeSuspend)
        .flatMap { createPublisher(for: $0) }
        .eraseToAnyPublisher()
}

/// Creates an `AnyPublisher` for the provided `NativeSuspend` that returns a `NativeFlow`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: A publisher that publishes the collected values.
public func createPublisher<Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<NativeUnit?, Failure>, Failure>
) -> AnyPublisher<Void, Failure> {
    return createFuture(for: nativeSuspend)
        .flatMap { createPublisher(for: $0) }
        .eraseToAnyPublisher()
}
