//
//  AsyncFunction.swift
//  KMPNativeCoroutinesAsync
//
//  Created by Rick Clephas on 13/06/2021.
//

import Dispatch
import KMPNativeCoroutinesCore

internal let RETURN_TYPE_SWIFT_ASYNC = "swift-async"

/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The result from the `nativeSuspend`.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Result, Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<Result, Failure>
) async throws -> Result {
    if let function = nativeSuspend(RETURN_TYPE_SWIFT_ASYNC, EmptyNativeCallback1, EmptyNativeCallback1, EmptyNativeCallback1)() {
        return try await (function as! (@Sendable () async throws -> Result))()
    }
    return try await AsyncFunctionTask(nativeSuspend: nativeSuspend).awaitResult()
}

/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Failure: Error>(
    for nativeSuspend: @escaping NativeSuspend<NativeUnit?, Failure>
) async throws -> Void {
    _ = try await AsyncFunctionTask(nativeSuspend: nativeSuspend).awaitResult()
}

private class AsyncFunctionTask<Result, Failure: Error>: @unchecked Sendable {
    
    private let semaphore = DispatchSemaphore(value: 1)
    private var nativeCancellable: NativeCancellable?
    private var result: Result? = nil
    private var error: Failure? = nil
    private var cancellationError: Failure? = nil
    private var continuation: UnsafeContinuation<Result, Error>? = nil
    
    init(nativeSuspend: NativeSuspend<Result, Failure>) {
        nativeCancellable = nativeSuspend(nil, { result in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.result = result
            if let continuation = self.continuation {
                continuation.resume(returning: result)
                self.continuation = nil
            }
            self.nativeCancellable = nil
            return nil
        }, { error in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.error = error
            if let continuation = self.continuation {
                continuation.resume(throwing: error)
                self.continuation = nil
            }
            self.nativeCancellable = nil
            return nil
        }, { cancellationError in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.cancellationError = cancellationError
            if let continuation = self.continuation {
                continuation.resume(throwing: CancellationError())
                self.continuation = nil
            }
            self.nativeCancellable = nil
            return nil
        })
    }
    
    func awaitResult() async throws -> Result {
        try await withTaskCancellationHandler {
            try await withUnsafeThrowingContinuation { continuation in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                if let result = self.result {
                    continuation.resume(returning: result)
                } else if let error = self.error {
                    continuation.resume(throwing: error)
                } else if self.cancellationError != nil {
                    continuation.resume(throwing: CancellationError())
                } else {
                    guard self.continuation == nil else {
                        fatalError("Concurrent calls to awaitResult aren't supported")
                    }
                    self.continuation = continuation
                }
            }
        } onCancel: {
            _ = nativeCancellable?()
            nativeCancellable = nil
        }
    }
}
