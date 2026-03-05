//
//  AsyncSequence.swift
//  AsyncSequence
//
//  Created by Rick Clephas on 06/03/2022.
//

import Dispatch
import KMPNativeCoroutinesCore

/// Wraps the `NativeFlow` in a `NativeFlowAsyncSequence`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: A `NativeFlowAsyncSequence` that yields the collected values.
public func asyncSequence<Output, Failure: Error, Unit>(
    for nativeFlow: @escaping NativeFlow<Output, Failure, Unit>
) -> NativeFlowAsyncSequence<Output, Error, Unit> {
    return NativeFlowAsyncSequence(nativeFlow: nativeFlow)
}

/// Simple wrapper to satisfy Swift 6 Sendable checks when values cross concurrency boundaries.
/// Use only when `Output` is effectively immutable (typical for KMM models).
public struct UncheckedSendable<T>: @unchecked Sendable {
    public let value: T
    public init(_ value: T) { self.value = value }
}

public struct NativeFlowAsyncSequence<Output, Failure: Error, Unit>: AsyncSequence {
    public typealias Element = Output
    
    var nativeFlow: NativeFlow<Output, Failure, Unit>
    
    public class Iterator: AsyncIteratorProtocol, @unchecked Sendable {
        
        private let semaphore = DispatchSemaphore(value: 1)
        private var nativeCancellable: NativeCancellable<Unit>?
        
        // Store the next item + "next" callback. Wrapped to satisfy Swift 6 Sendable checks.
        private var item: (UncheckedSendable<Output>, () -> Unit)? = nil
        
        // Completion result from native flow
        private var result: Failure?? = Optional.none
        
        // Cancellation from native flow
        private var cancellationError: Failure? = nil
        
        // Continuation used to suspend/resume `next()`. Wrapped to satisfy Swift 6 Sendable checks.
        private var continuation: UnsafeContinuation<UncheckedSendable<Output>?, Error>? = nil
        
        @Sendable
        init(nativeFlow: NativeFlow<Output, Failure, Unit>) {
            nativeCancellable = nativeFlow({ [weak self] item, next, unit  in
                guard let self else { return unit }
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                
                if let continuation = self.continuation {
                    continuation.resume(returning: UncheckedSendable(item))
                    self.continuation = nil
                    return next()
                } else {
                    self.item = (UncheckedSendable(item), next)
                    return unit
                }
                
            }, { [weak self] error, unit in
                guard let self else { return unit }
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                
                self.result = Optional.some(error)
                if let continuation = self.continuation {
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else {
                        continuation.resume(returning: nil)
                    }
                    self.continuation = nil
                }
                self.nativeCancellable = nil
                return unit
                
            }, { [weak self] cancellationError, unit in
                guard let self else { return unit }
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                
                self.cancellationError = cancellationError
                if let continuation = self.continuation {
                    continuation.resume(returning: nil)
                    self.continuation = nil
                }
                self.nativeCancellable = nil
                return unit
            })
        }
        
        public func next() async throws -> Output? {
            let wrapped: UncheckedSendable<Output>? = try await withTaskCancellationHandler {
                try await withUnsafeThrowingContinuation { (continuation: UnsafeContinuation<UncheckedSendable<Output>?, Error>) in
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
                        
                    } else if self.cancellationError != nil {
                        continuation.resume(throwing: CancellationError())
                        
                    } else {
                        guard self.continuation == nil else {
                            fatalError("Concurrent calls to next aren't supported")
                        }
                        self.continuation = continuation
                    }
                }
            } onCancel: {
                _ = nativeCancellable?()
                nativeCancellable = nil
            }
            
            return wrapped?.value
        }
        
        deinit {
            _ = nativeCancellable?()
            nativeCancellable = nil
        }
    }
    
    public func makeAsyncIterator() -> Iterator {
        return Iterator(nativeFlow: nativeFlow)
    }
}
