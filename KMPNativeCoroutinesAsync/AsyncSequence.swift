//
//  AsyncSequence.swift
//  AsyncSequence
//
//  Created by Rick Clephas on 06/03/2022.
//

import Dispatch
import KMPNativeCoroutinesCore

private let RETURN_TYPE_SWIFT_ASYNC_SEQUENCE = "swift-async-sequence"

/// Wraps the `NativeFlow` in an `AsyncSequence`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An `AsyncSequence` that yields the collected values.
public func asyncSequence<Output, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Output, Failure>
) -> AnyAsyncSequence<Output> {
    if let sequence = nativeFlow(RETURN_TYPE_SWIFT_ASYNC_SEQUENCE, EmptyNativeCallback2, EmptyNativeCallback, EmptyNativeCallback)() {
        return sequence as! AnyAsyncSequence<Output>
    }
    return AnyAsyncSequence(NativeFlowAsyncSequence(nativeFlow: nativeFlow))
}

public extension AsyncSequence {
    /// Creates a `NativeFlow` for this `AsyncSequence`.
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

private struct NativeFlowAsyncSequence<Output, Failure: Error>: AsyncSequence {
    typealias AsyncIterator = Iterator
    
    public typealias Element = Output
    
    var nativeFlow: NativeFlow<Output, Failure>
    
    public class Iterator: AsyncIteratorProtocol, @unchecked Sendable {
        
        private let semaphore = DispatchSemaphore(value: 1)
        private var nativeCancellable: NativeCancellable?
        private var item: (Output, () -> NativeUnit)? = nil
        private var result: Failure?? = Optional.none
        private var cancellationError: Failure? = nil
        private var continuation: UnsafeContinuation<Output?, Error>? = nil
        
        init(nativeFlow: NativeFlow<Output, Failure>) {
            nativeCancellable = nativeFlow(nil, { item, next, unit in
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
            }, { cancellationError, unit in
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
            return try await withTaskCancellationHandler {
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
        }
    }
    
    public func makeAsyncIterator() -> Iterator {
        return Iterator(nativeFlow: nativeFlow)
    }
}

