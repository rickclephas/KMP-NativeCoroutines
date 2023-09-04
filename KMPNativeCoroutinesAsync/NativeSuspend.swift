//
//  NativeSuspend.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 04/09/2023.
//

import KMPNativeCoroutinesCore

/// Creates a `NativeSuspend` for the provided async operation.
public func nativeSuspend<Result>(
    priority: TaskPriority? = nil,
    operation: @escaping @Sendable () async throws -> Result
) -> NativeSuspend<Result, Error> {
    return { returnType, onResult, onError, onCancelled in
        if returnType == RETURN_TYPE_SWIFT_ASYNC {
            return { operation }
        } else if returnType != nil {
            return { nil }
        }
        let task = Task(priority: priority) {
            do {
                let result = try await operation()
                _ = onResult(result, ())
            } catch {
                if error is CancellationError {
                    _ = onCancelled(error, ())
                } else {
                    _ = onError(error, ())
                }
            }
        }
        return task.asNativeCancellable()
    }
}
