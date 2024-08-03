//
//  AsyncError.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 11/05/2024.
//

import KMPNativeCoroutinesCore

/// Awaits the `NativeSuspend` and returns the optional error.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The `Error` from the `nativeSuspend`, or `nil`.
public func asyncError<Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeUnit?, Failure>
) async -> Error? {
    do {
        try await asyncFunction(for: nativeSuspend)
        return nil
    } catch {
        return error
    }
}
