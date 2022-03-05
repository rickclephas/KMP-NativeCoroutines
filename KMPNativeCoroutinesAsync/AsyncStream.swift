//
//  AsyncStream.swift
//  AsyncStream
//
//  Created by Rick Clephas on 15/07/2021.
//

import Dispatch
import KMPNativeCoroutinesCore

/// Wraps the `NativeFlow` in an `AsyncThrowingStream`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An stream that yields the collected values.
public func asyncStream<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> AsyncThrowingStream<Output, Error> {
    let iterator = NativeFlowAsyncIterator(nativeFlow: nativeFlow)
    return AsyncThrowingStream { try await iterator.next() }
}

private class NativeFlowAsyncIterator<Output, Failure: Error, Unit> : AsyncIteratorProtocol {
    
    private let semaphore = DispatchSemaphore(value: 1)
    private var nativeCancellable: NativeCancellable<Unit>?
    private var isActive: Bool = true
    private var item: (Output, () -> Unit)? = nil
    private var result: Failure?? = Optional.none
    private var cancellationError: Failure? = nil
    private var continuation: UnsafeContinuation<Output?, Error>? = nil
    
    init(nativeFlow: NativeFlow<Output, Failure, Unit>) {
        nativeCancellable = nativeFlow({ item, next, unit in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            if let continuation = self.continuation {
                continuation.resume(returning: item)
                self.continuation = nil
                return next()
            } else {
                self.item = (item, next)
                return unit
            }
        }, { error, unit in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            if let continuation = self.continuation {
                if let error = error {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume(returning: nil)
                }
                self.continuation = nil
                self.isActive = false
                self.nativeCancellable = nil
            } else {
                self.result = Optional.some(error)
            }
            return unit
        }, { cancellationError, unit in
            self.semaphore.wait()
            defer { self.semaphore.signal() }
            self.cancellationError = cancellationError
            if let continuation = self.continuation {
                continuation.resume(returning: nil)
                self.continuation = nil
                self.isActive = false
                self.nativeCancellable = nil
            }
            return unit
        })
    }
    
    func next() async throws -> Output? {
        guard isActive else { return nil }
        return try await withTaskCancellationHandler {
            _ = nativeCancellable?()
            nativeCancellable = nil
        } operation: {
            try await withUnsafeThrowingContinuation { continuation in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                if let (item, next) = self.item {
                    continuation.resume(returning: item)
                    _ = next()
                    self.item = nil
                } else if let result = self.result {
                    if let error = result {
                        continuation.resume(throwing: error)
                    } else {
                        continuation.resume(returning: nil)
                    }
                    self.result = Optional.none
                    self.isActive = false
                    self.nativeCancellable = nil
                } else if self.cancellationError != nil {
                    continuation.resume(returning: nil)
                    self.cancellationError = nil
                    self.isActive = false
                    self.nativeCancellable = nil
                } else if !self.isActive {
                    continuation.resume(returning: nil)
                } else {
                    guard self.continuation == nil else {
                        fatalError("") // TODO: Add error message
                    }
                    self.continuation = continuation
                }
            }
        }
    }
}
