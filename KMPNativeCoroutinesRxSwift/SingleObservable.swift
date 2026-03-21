//
//  SingleObservable.swift
//  KMPNativeCoroutinesRxSwift
//
//  Created by Rick Clephas on 05/07/2021.
//

import RxSwift
import KMPNativeCoroutinesCore

/// Creates an `Observable` for the provided `NativeSuspend` that returns a `NativeFlow`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Output, Failure: Error, Unit>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<Output, Failure, Unit>, Failure, Unit>
) -> Observable<Output> {
    return createSingle(for: nativeSuspend)
        .asObservable()
        .flatMap { createObservable(for: $0) }
}

/// This function provides source compatibility during the migration to Swift export.
///
/// You should migrate away from this function once you have fully migrated to Swift export.
@available(*, deprecated, message: "Kotlin Coroutines are supported by Swift export")
@available(iOS 13.0, macOS 10.15, tvOS 13.0, watchOS 6.0, *)
public func createObservable<Sequence: AsyncSequence>(
    for asyncSequence: @escaping () async throws -> Sequence
) -> Observable<Sequence.Element> {
    return createSingle(for: asyncSequence)
        .asObservable()
        .flatMap { createObservable(for: $0) }
}

/// Creates an `Observable` for the provided `NativeSuspend` that returns a `NativeFlow`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Unit, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<Unit, Failure, Unit>, Failure, Unit>
) -> Observable<Void> {
    return createSingle(for: nativeSuspend)
        .asObservable()
        .flatMap { createObservable(for: $0) }
}
