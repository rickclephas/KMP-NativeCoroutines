//
//  NativeFlow.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A function that collects a Kotlin coroutines Flow via callbacks.
///
/// The function takes an `onItem`, `onComplete` and `onCancelled` callback
/// and returns a cancellable that can be used to cancel the collection.
///
/// When `returnType` isn't `nil` the returned cancellable will return the requested type,
/// or `nil` if the requested type isn't supported by this `NativeFlow`.
public typealias NativeFlow<Output, Failure: Error> = (
    _ returnType: String?,
    _ onItem: @escaping NativeCallback2<Output, NativeCallback>,
    _ onComplete: @escaping NativeCallback1<Failure?>,
    _ onCancelled: @escaping NativeCallback1<Failure>
) -> NativeCancellable
