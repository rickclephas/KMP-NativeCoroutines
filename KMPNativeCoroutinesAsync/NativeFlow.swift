//
//  NativeFlow.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 04/09/2023.
//

import KMPNativeCoroutinesCore

public extension AsyncSequence {
    /// Creates a `NativeFlow` for this `AsyncSequence`.
    /// - Parameter priority: The priority of the task collecting this `AsyncSequence`.
    func asNativeFlow(priority: TaskPriority? = nil) -> NativeFlow<Element, Error> {
        return { returnType, onItem, onComplete, onCancelled in
            if returnType == RETURN_TYPE_SWIFT_ASYNC_SEQUENCE {
                return { AnyAsyncSequence(self) }
            } else if returnType != nil {
                return { nil }
            }
            let task = Task(priority: priority) {
                do {
                    for try await value in self {
                        await withUnsafeContinuation { continuation in
                            _ = onItem(value, { continuation.resume() }, ())
                        }
                    }
                    try Task.checkCancellation()
                    _ = onComplete(nil, ())
                } catch {
                    if error is CancellationError {
                        _ = onCancelled(error, ())
                    } else {
                        _ = onComplete(error, ())
                    }
                }
            }
            return task.asNativeCancellable()
        }
    }
}
