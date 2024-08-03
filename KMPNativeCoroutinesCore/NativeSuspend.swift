//
//  NativeSuspend.swift
//  KMPNativeCoroutinesCore
//
//  Created by Rick Clephas on 06/06/2021.
//

/// A function that awaits a suspend function via callbacks.
///
/// The function takes an `onResult`, `onError` and `onCancelled` callback
/// and returns a cancellable that can be used to cancel the suspend function.
///
/// When `returnType` isn't `nil` the returned cancellable will return the requested type,
/// or `nil` if the requested type isn't supported by this `NativeSuspend`.
public typealias NativeSuspend<Result, Failure: Error> = (
    _ returnType: String?,
    _ onResult: @escaping NativeCallback1<Result>,
    _ onError: @escaping NativeCallback1<Failure>,
    _ onCancelled: @escaping NativeCallback1<Failure>
) -> NativeCancellable
