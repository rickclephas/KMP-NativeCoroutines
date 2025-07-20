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

public struct NativeFlowAsyncSequence<Output, Failure: Error, Unit>: AsyncSequence {
    public typealias Element = Output
    
    var nativeFlow: NativeFlow<Output, Failure, Unit>
    
    public class Iterator: AsyncIteratorProtocol, @unchecked Sendable {
        
        private enum State {
            case new(NativeFlow<Output, Failure, Unit>)
            case producing(UnsafeContinuation<Output?, Error>)
            case consuming(() -> Unit)
            case completed(Failure?)
            case cancelled
        }
        
        private let semaphore = DispatchSemaphore(value: 1)
        private var state: State
        private var nativeCancellable: NativeCancellable<Unit>?
        
        init(nativeFlow: @escaping NativeFlow<Output, Failure, Unit>) {
            state = .new(nativeFlow)
        }
        
        private func onItem(item: Output, next: @escaping () -> Unit, unit: Unit) -> Unit {
            semaphore.wait()
            defer { semaphore.signal() }
            switch state {
            case .new:
                fatalError("onItem can't be called while in state new")
            case .producing(let continuation):
                continuation.resume(returning: item)
                state = .consuming(next)
                return unit
            case .consuming:
                fatalError("onItem can't be called while in state consuming")
            case .completed:
                fatalError("onItem can't be called while in state completed")
            case .cancelled:
                fatalError("onItem can't be called while in state cancelled")
            }
        }
        
        private func onComplete(error: Failure?, unit: Unit) -> Unit {
            semaphore.wait()
            defer { semaphore.signal() }
            switch state {
            case .new:
                fatalError("onComplete can't be called while in state new")
            case .producing(let continuation):
                if let error {
                    continuation.resume(throwing: error)
                } else {
                    continuation.resume(returning: nil)
                }
                state = .completed(error)
                return unit
            case .consuming:
                state = .completed(error)
                return unit
            case .completed:
                return unit
            case .cancelled:
                return unit
            }
        }
        
        private func onCancelled(error: Failure, unit: Unit) -> Unit {
            semaphore.wait()
            defer { semaphore.signal() }
            switch state {
            case .new:
                fatalError("onCancelled can't be called while in state new")
            case .producing(let continuation):
                continuation.resume(throwing: CancellationError())
                state = .cancelled
                return unit
            case .consuming:
                state = .cancelled
                return unit
            case .completed:
                return unit
            case .cancelled:
                return unit
            }
        }
        
        public func next() async throws -> Output? {
            return try await withTaskCancellationHandler {
                try await withUnsafeThrowingContinuation { continuation in
                    self.semaphore.wait()
                    defer { self.semaphore.signal() }
                    switch state {
                    case .new(let nativeFlow):
                        nativeCancellable = nativeFlow(onItem, onComplete, onCancelled)
                        state = .producing(continuation)
                    case .producing:
                        fatalError("Concurrent calls to next aren't supported")
                    case .consuming(let next):
                        _ = next()
                        state = .producing(continuation)
                    case .completed(let error):
                        if let error {
                            continuation.resume(throwing: error)
                        } else {
                            continuation.resume(returning: nil)
                        }
                    case .cancelled:
                        continuation.resume(throwing: CancellationError())
                    }
                }
            } onCancel: {
                self.semaphore.wait()
                if case .new = state {
                    state = .cancelled
                }
                let nativeCancellable = self.nativeCancellable
                self.nativeCancellable = nil
                self.semaphore.signal()
                _ = nativeCancellable?()
            }
        }
    }
    
    public func makeAsyncIterator() -> Iterator {
        return Iterator(nativeFlow: nativeFlow)
    }
}

