//
//  AsyncFunction.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 13/06/2021.
//

import Foundation
import KMPNativeCoroutinesCore

/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The result from the `nativeSuspend`.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Result, Failure: Error, Unit>(for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>) async throws -> Result {
    let cancellableActor = NativeCancellableActor<Unit>()
    return try await withTaskCancellationHandler {
        async { await cancellableActor.cancel() }
    } operation: {
        try await withUnsafeThrowingContinuation { continuation in
            let nativeCancellable = nativeSuspend({ output, unit in
                continuation.resume(returning: output)
                return unit
            }, { error, unit in
                continuation.resume(throwing: error)
                return unit
            })
            async { await cancellableActor.setNativeCancellable(nativeCancellable) }
        }
    }
}

internal actor NativeCancellableActor<Unit> {
    
    private var isCancelled = false
    private var nativeCancellable: NativeCancellable<Unit>? = nil
    
    func setNativeCancellable(_ nativeCancellable: @escaping NativeCancellable<Unit>) {
        guard !isCancelled else {
            _ = nativeCancellable()
            return
        }
        self.nativeCancellable = nativeCancellable
    }
    
    func cancel() {
        isCancelled = true
        _ = nativeCancellable?()
        nativeCancellable = nil
    }
}
