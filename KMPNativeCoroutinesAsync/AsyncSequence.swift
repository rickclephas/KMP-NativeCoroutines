//
//  AsyncSequence.swift
//  AsyncSequence
//
//  Created by Rick Clephas on 06/03/2022.
//

import Dispatch
import KMPNativeCoroutinesCore

internal let RETURN_TYPE_SWIFT_ASYNC_SEQUENCE = "swift-async-sequence"

/// Wraps the `NativeFlow` in an `AsyncSequence`.
/// - Parameter nativeFlow: The native flow to collect.
/// - Returns: An `AsyncSequence` that yields the collected values.
public func asyncSequence<Output, Failure: Error>(
    for nativeFlow: @escaping NativeFlow<Output, Failure>
) -> AnyAsyncSequence<Output> {
    if let sequence = nativeFlow(RETURN_TYPE_SWIFT_ASYNC_SEQUENCE, EmptyNativeCallback2, EmptyNativeCallback1, EmptyNativeCallback1)() {
        return sequence as! AnyAsyncSequence<Output>
    }
    return AnyAsyncSequence(NativeFlowAsyncSequence(nativeFlow: nativeFlow))
}

private struct NativeFlowAsyncSequence<Output, Failure: Error>: AsyncSequence {
    typealias AsyncIterator = Iterator
    
    public typealias Element = Output
    
    var nativeFlow: NativeFlow<Output, Failure>
    
    public class Iterator: AsyncIteratorProtocol, @unchecked Sendable {
        
        private let semaphore = DispatchSemaphore(value: 1)
        private var nativeCancellable: NativeCancellable?
        private var item: (Output, NativeCallback)? = nil
        private var result: Failure?? = Optional.none
        private var cancellationError: Failure? = nil
        private var continuation: UnsafeContinuation<Output?, Error>? = nil
        
        init(nativeFlow: NativeFlow<Output, Failure>) {
            nativeCancellable = nativeFlow(nil, { item, next in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                if let continuation = self.continuation {
                    continuation.resume(returning: item)
                    self.continuation = nil
                    return next()
                } else {
                    self.item = (item, next)
                    return nil
                }
            }, { error in
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
                return nil
            }, { cancellationError in
                self.semaphore.wait()
                defer { self.semaphore.signal() }
                self.cancellationError = cancellationError
                if let continuation = self.continuation {
                    continuation.resume(returning: nil)
                    self.continuation = nil
                }
                self.nativeCancellable = nil
                return nil
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
