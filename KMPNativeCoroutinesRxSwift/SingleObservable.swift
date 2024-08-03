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
public func createObservable<Output, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<Output, Failure>, Failure>
) -> Observable<Output> {
    return createSingle(for: nativeSuspend)
        .asObservable()
        .flatMap { createObservable(for: $0) }
}

/// Creates an `Observable` for the provided `NativeSuspend` that returns a `NativeFlow`.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: An observable that publishes the collected values.
public func createObservable<Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeFlow<NativeUnit?, Failure>, Failure>
) -> Observable<Void> {
    return createSingle(for: nativeSuspend)
        .asObservable()
        .flatMap { createObservable(for: $0) }
}
